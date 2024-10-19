package com.ribuufing.findlostitem.di

import com.google.firebase.firestore.FirebaseFirestore
import com.ribuufing.findlostitem.data.datasources.FirestoreDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance() // Firebase'i burada başlatmıyorsunuz
    }

    @Provides
    @Singleton
    fun provideFirestoreDataSource(firestore: FirebaseFirestore): FirestoreDataSource {
        return FirestoreDataSource(firestore)
    }
}
