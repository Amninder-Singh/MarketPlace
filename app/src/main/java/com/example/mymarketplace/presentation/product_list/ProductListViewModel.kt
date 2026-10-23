package com.example.mymarketplace.presentation.product_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymarketplace.data.sync.SyncManager
import com.example.mymarketplace.domain.usecase.GetProductsUseCase
import com.example.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val syncManager: SyncManager
) :
    ViewModel() {

    private val _selectedCategory = MutableStateFlow(ProductListState().selectedCategory)
    private val _searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(ProductListState(isLoading = true))
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    private val _filters = combine(_selectedCategory, _searchQuery) { category, query ->
        category to query
    }
        .debounce(300L)
        .distinctUntilChanged()

    init {
        syncManager.scheduleBackgroundSync()

        viewModelScope.launch {
            syncManager.refreshDataNow()
        }

        observeStateChanges()
    }

    private fun observeStateChanges() {
        // Observe Sync State
        viewModelScope.launch {
            syncManager.observeSyncState().collect { syncStatus ->
                _state.update { it.copy(syncState = syncStatus) }
            }
        }

        // Observe Product List
        viewModelScope.launch {
            _filters.flatMapLatest { (category, query) ->
                getProductsUseCase(category, query)
            }.collect { products ->
                _state.update { it.copy(products = products, isLoading = false) }
            }
        }
    }

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.OnSearchQueryChange -> {
                _searchQuery.value = event.query
                _state.update { it.copy(searchQuery = event.query) }
            }
            is ProductListEvent.OnSelectCategory -> {
                _selectedCategory.value = event.category
                _state.update { it.copy(selectedCategory = event.category) }
            }
            is ProductListEvent.OnToggleFavorite -> {
                viewModelScope.launch {
                    toggleFavoriteUseCase(event.productId)
                }
            }
            ProductListEvent.OnRefreshSync -> {
                viewModelScope.launch {
                    syncManager.refreshDataNow()
                }
            }
        }
    }
}
