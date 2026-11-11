package com.project.knotbook.di

import com.project.knotbook.repository.AuthRepository
import com.project.knotbook.repository.AuthRepositoryImpl
import com.project.knotbook.repository.DashboardRepository
import com.project.knotbook.repository.DashboardRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds  //whenever authrepo is needed provide authrepoImpl
    @Singleton
    abstract fun bindAuthRepository(
        implementation: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        impl: DashboardRepositoryImpl
    ): DashboardRepository

}