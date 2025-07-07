package com.example.poc_filtro_ia.ui.screen.start

import android.net.Uri

data class ImageState(
    val isLoading: Boolean = false,
    val photoUris: Uri? = null,
    val label: String = "",
    val confidence: Float = 0f
)