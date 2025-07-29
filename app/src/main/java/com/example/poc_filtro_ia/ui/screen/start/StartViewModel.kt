package com.example.poc_filtro_ia.ui.screen.start

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.poc_filtro_ia.ComposeFileProvider
import com.example.poc_filtro_ia.Models.CategoryClassifier
import com.example.poc_filtro_ia.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import androidx.core.graphics.scale

class StartViewModel : ViewModel(){
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val _imageState: MutableStateFlow<ImageState> = MutableStateFlow(ImageState())
    val imageState: StateFlow<ImageState> = _imageState.asStateFlow()


    fun classifierImage(bitmap: Bitmap, context: Context) {
        _uiState.value = UiState.LoadingTensorFlow

        try {

            val convertedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val inputSize = 224
            val bitmapScaled = convertedBitmap.scale(inputSize, inputSize)

            val classifier = CategoryClassifier(context)
            val (label, confidence) = classifier.classify(bitmapScaled)

            _imageState.value = ImageState(
                label = label,
                confidence = confidence,
                photoUris = _imageState.value.photoUris,
                isAnalyzed = true
            )

            _uiState.value = UiState.SuccessTensorFlow(label, confidence)
            Log.d("StartViewModel", "Clasificación: $label (${confidence * 100}%)")


        } catch (e: IOException) {
            _uiState.value = UiState.Error(e.localizedMessage ?: "")
        }

    }

    fun handlePhotoResult(success: Boolean, uri: Uri, context: Context) {
        if (success) {
            takePhoto(uri)
        } else {
            viewModelScope.launch {
                ComposeFileProvider.deleteFileFromCacheDirImages(context, uri)
            }
        }
    }

    suspend fun preparePhotoUri(context: Context): Uri {
        return ComposeFileProvider.Companion.generateImageUri(context)
    }

    fun changeCategory(){
        _imageState.value = _imageState.value.copy(
            isAnalyzed = false
        )
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