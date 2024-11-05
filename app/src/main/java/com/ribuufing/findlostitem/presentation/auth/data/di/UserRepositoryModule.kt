package com.ribuufing.findlostitem.presentation.auth.data.di

import com.ribuufing.findlostitem.presentation.auth.data.repository.UserRepositoryImpl
import com.ribuufing.findlostitem.presentation.auth.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}