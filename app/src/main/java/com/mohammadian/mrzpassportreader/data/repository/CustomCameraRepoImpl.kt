package com.mohammadian.mrzpassportreader.data.repository

import android.app.Application
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import com.innovatrics.mrz.MrzRecord
import com.mohammadian.mrzpassportreader.domain.repository.CustomCameraRepo
import com.mohammadian.mrzpassportreader.util.TextReaderAnalyzer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import javax.inject.Inject

class CustomCameraRepoImpl @Inject constructor(
    private val cameraProvider: ProcessCameraProvider,
    private val selector: CameraSelector,
    private val preview: Preview,
    private val imageAnalysis: ImageAnalysis,
    private val imageCapture: ImageCapture,
    private val cameraExecutor: ExecutorService,
    private val context: Application

) : CustomCameraRepo {

    override suspend fun showCameraPreview(
        previewView: PreviewView, lifecycleOwner: LifecycleOwner
    ): Flow<MrzRecord> {
        return callbackFlow {
            preview.setSurfaceProvider(previewView.surfaceProvider)
            try {
                cameraProvider.unbindAll()
                imageAnalysis.apply {
                    setAnalyzer(cameraExecutor, TextReaderAnalyzer(context) {
                        launch {
                            send(it)
                        }

                    })
                }
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, selector, preview, imageAnalysis, imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            awaitClose {
                cameraExecutor.shutdown()
                cameraProvider.unbindAll()
            }
        }

    }

    override suspend fun dismissCameraPreview() {
        cameraProvider.unbindAll()
        cameraExecutor.shutdown()
    }

}