package com.ribuufing.findlostitem.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.ribuufing.findlostitem.data.LostItemRepositoryImpl
import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import com.ribuufing.findlostitem.domain.repository.LostItemRepository
import com.ribuufing.findlostitem.presentation.chat.data.ChatRepositoryImpl
import com.ribuufing.findlostitem.presentation.chat.domain.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLostItemRepository(
        dataSource: FirestoreDataSource,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): LostItemRepository = LostItemRepositoryImpl(dataSource, auth, storage)

    @Provides
    @Singleton
    fun provideChatRepository(
        dataSource: FirestoreDataSource
    ): ChatRepository = ChatRepositoryImpl(dataSource)
}