package com.ribuufing.findlostitem.di

import com.ribuufing.findlostitem.data.LostItemRepositoryImpl
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import com.ribuufing.findlostitem.presentation.chat.data.ChatRepositoryImpl
import com.ribuufing.findlostitem.presentation.chat.domain.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindLostItemRepository(impl: LostItemRepositoryImpl): LostItemRepository

    @Binds
    @Singleton
    abstract fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository

}