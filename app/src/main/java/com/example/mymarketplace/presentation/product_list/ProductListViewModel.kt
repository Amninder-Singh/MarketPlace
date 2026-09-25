package com.example.mymarketplace.presentation.product_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymarketplace.data.sync.SyncManager
import com.example.mymarketplace.domain.usecase.GetProductsUseCase
import com.example.mymarketplace.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(ProductListState().selectedCategory)
    private val _searchQuery = MutableStateFlow("")

    init {
        syncManager.scheduleBackgroundSync()
        viewModelScope.launch {
            syncManager.refreshDataNow()
        }
    }

    private val _products = combine(_selectedCategory, _searchQuery) { category, query ->
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        getProductsUseCase(category, query)
    }

    val state: StateFlow<ProductListState> = combine(
        _products,
        _selectedCategory,
        _searchQuery,
        syncManager.observeSyncState()
    ) { products, category, query, syncState ->
        ProductListState(
            products = products,
            selectedCategory = category,
            searchQuery = query,
            syncState = syncState,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProductListState(isLoading = true)
    )

    fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.OnSearchQueryChange -> {
                _searchQuery.value = event.query
            }
            is ProductListEvent.OnSelectCategory -> {
                _selectedCategory.value = event.category
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
