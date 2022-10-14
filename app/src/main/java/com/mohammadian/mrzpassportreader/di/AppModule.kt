package com.mohammadian.mrzpassportreader.di

import android.app.Application
import androidx.camera.core.AspectRatio.RATIO_16_9
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_AUTO
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    @ViewModelScoped
    fun provideCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(LENS_FACING_BACK)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideCameraProvider(application: Application): ProcessCameraProvider {
        return ProcessCameraProvider.getInstance(application).get()
    }

    @Provides
    @ViewModelScoped
    fun provideCameraPreview(): Preview {
        return Preview.Builder().build()
    }

    @Provides
    @ViewModelScoped
    fun provideImageCapture(): ImageCapture {
        return ImageCapture.Builder()
            .setFlashMode(FLASH_MODE_AUTO)
            .setTargetAspectRatio(RATIO_16_9)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideImageAnalysis(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideCameraExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }
}