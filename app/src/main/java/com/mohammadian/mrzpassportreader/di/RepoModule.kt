package com.mohammadian.mrzpassportreader.di

import com.mohammadian.mrzpassportreader.data.repository.CustomCameraRepoImpl
import com.mohammadian.mrzpassportreader.domain.repository.CustomCameraRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindCameraRepo(
        customCameraRepoImpl: CustomCameraRepoImpl
    ): CustomCameraRepo

}