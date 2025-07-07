package com.example.poc_filtro_ia.Models

import android.graphics.Bitmap

interface ImageClassifierInterface {
    fun classify(bitmap: Bitmap): Pair<String, Float>
}