package com.mohammadian.mrzpassportreader.di

import com.mohammadian.mrzpassportreader.data.repository.CustomCameraRepoImpl
import com.mohammadian.mrzpassportreader.domain.repository.CustomCameraRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepoModule {

    @Binds
    @ViewModelScoped
    abstract fun bindCameraRepo(
        customCameraRepoImpl: CustomCameraRepoImpl
    ): CustomCameraRepo

}