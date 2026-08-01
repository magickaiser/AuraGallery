package com.aura.gallery.di

import com.aura.gallery.data.repository.FavoriteRepositoryImpl
import com.aura.gallery.data.repository.MediaRepositoryImpl
import com.aura.gallery.data.repository.TrashRepositoryImpl
import com.aura.gallery.domain.repository.FavoriteRepository
import com.aura.gallery.domain.repository.MediaRepository
import com.aura.gallery.domain.repository.TrashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        impl: MediaRepositoryImpl
    ): MediaRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindTrashRepository(
        impl: TrashRepositoryImpl
    ): TrashRepository
}
