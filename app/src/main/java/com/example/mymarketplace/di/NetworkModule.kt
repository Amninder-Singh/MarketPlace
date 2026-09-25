package com.example.mymarketplace.di

import com.example.mymarketplace.data.remote.api.FakeMarketplaceApi
import com.example.mymarketplace.data.remote.api.MarketplaceApi
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    @Singleton
    abstract fun bindMarketplaceApi(
        fakeMarketplaceApi: FakeMarketplaceApi
    ): MarketplaceApi
}
