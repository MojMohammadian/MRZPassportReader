package com.mohammadian.mrzpassportreader.presentation

import android.app.Application
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammadian.mrzpassportreader.domain.repository.CustomCameraRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: CustomCameraRepo
) : ViewModel() {
    @Inject
    lateinit var context: Application
    fun showCameraView(
        previewView: PreviewView, lifecycleOwner: LifecycleOwner
    ) {

        viewModelScope.launch {
            repo.showCameraPreview(previewView, lifecycleOwner).collectLatest {

                val mrz = """
                    name: ${it.givenNames}
                    surname: ${it.surname}
                    date of birth: ${it.dateOfBirth}
                    passport number: ${it.documentNumber}
                """.trimIndent()
                //This Line of code dose not correct in MVVM Clean architecture
                //You Should not handle Toasts in view Model
                //Hear is the mrz data extracted
                Toast.makeText(context, mrz, Toast.LENGTH_LONG).show()
                repo.dismissCameraPreview()
            }
        }
    }
}