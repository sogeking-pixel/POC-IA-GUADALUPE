package com.example.poc_filtro_ia.Models

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class CategoryClassifier(context: Context):
BaseImageClassifier(context, "ImagenClassifier/model_classifier.tflite", "ImagenClassifier/labels.txt")
{

    override fun classify(bitmap: Bitmap): Pair<String, Float> {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        val tensorImage = TensorImage(DataType.UINT8)
        tensorImage.load(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.UINT8)

        interpreter.run(processedImage.buffer, outputBuffer.buffer.rewind())

        val probabilities = outputBuffer.floatArray
//        for (i in probabilities.indices) {
//            val label = labels[i]
//            val prob = probabilities[i]
//            Log.d("resultModelxd", "Label: $label - Probabilidad: $prob")
//        }
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val label = labels[maxIndex]
        val confidence = probabilities[maxIndex]

        return Pair(label, confidence)
    }



}