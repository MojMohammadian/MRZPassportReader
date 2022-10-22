package com.mohammadian.mrzpassportreader.presentation

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohammadian.mrzpassportreader.R
import com.mohammadian.mrzpassportreader.domain.repository.CustomCameraRepo
import com.mohammadian.mrzpassportreader.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: CustomCameraRepo
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun showCameraView(
        previewView: PreviewView, lifecycleOwner: LifecycleOwner
    ) {

        viewModelScope.launch {
            repo.showCameraPreview(previewView, lifecycleOwner).collectLatest {

                _eventFlow.emit(
                    UIEvent.ShowToast(
                        UiText.StringResource(R.string.mrz_message,it.givenNames,it.documentNumber)
                    )
                )

                repo.dismissCameraPreview()
            }
        }
    }

    sealed class UIEvent {
        data class ShowToast(val message: UiText) : UIEvent()
    }

}