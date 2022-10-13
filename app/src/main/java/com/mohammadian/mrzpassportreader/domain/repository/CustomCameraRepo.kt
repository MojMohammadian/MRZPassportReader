package com.mohammadian.mrzpassportreader.domain.repository

import android.content.Context
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.innovatrics.mrz.MrzRecord
import kotlinx.coroutines.flow.Flow

interface CustomCameraRepo {
//    suspend fun capture(context: Context)
    suspend fun showCameraPreview(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner): Flow<MrzRecord>
    suspend fun dismissCameraPreview()


}