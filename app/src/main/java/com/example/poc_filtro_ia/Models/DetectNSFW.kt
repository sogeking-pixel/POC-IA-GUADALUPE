package com.example.poc_filtro_ia.Models

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class DetectNSFW(context: Context):
    BaseImageClassifier(context, "DetectNSFW/NSFW.tflite", "DetectNSFW/labels.txt") {


    override fun classify(bitmap: Bitmap): Pair<String, Float> {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .add(NormalizeOp(0f, 255f))
            .build()

        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        val processedImage = imageProcessor.process(tensorImage)

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, labels.size), DataType.FLOAT32)

        interpreter.run(processedImage.buffer, outputBuffer.buffer.rewind())

        val probabilities = outputBuffer.floatArray

        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val label = labels[maxIndex]
        val confidence = probabilities[maxIndex]

        return Pair(label, confidence)
    }



}