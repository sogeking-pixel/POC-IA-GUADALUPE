package com.example.poc_filtro_ia.ui.screen.start

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.poc_filtro_ia.Models.CategoryClassifier
import com.example.poc_filtro_ia.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException

class StartViewModel : ViewModel(){
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val _imageState: MutableStateFlow<ImageState> = MutableStateFlow(ImageState())
    val imageState: StateFlow<ImageState> = _imageState.asStateFlow()


    fun classifierImage(bitmap: Bitmap, context: Context) {
        _uiState.value = UiState.LoadingTensorFlow
        Log.d("ModalCustom", "UiState is loading in view model")

        try {
            val classifier = CategoryClassifier(context)
            val (label, confidence) = classifier.classify(bitmap)

            _imageState.value = ImageState(
                label = label,
                confidence = confidence
            )

            _uiState.value = UiState.Success
            Log.d("StartViewModel", "Clasificación: $label (${confidence * 100}%)")


        } catch (e: IOException) {
            _uiState.value = UiState.Error(e.localizedMessage ?: "")
        }

    }





    fun takePhoto(photoUri: Uri) {
        _imageState.update { currentState -> currentState.copy(photoUris = photoUri) }
    }


    fun uriToBitmap(context: Context, imageUri: Uri?): Bitmap? {
        if (imageUri == null) return null
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}