package com.example.poc_filtro_ia.Models

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

abstract class BaseImageClassifier(
    context: Context,
    modelFilename: String,
    labelsFilename: String
) : ImageClassifierInterface {
    protected val interpreter: Interpreter
    protected val labels: List<String>

    init {
        interpreter = Interpreter(loadModelFile(context, modelFilename))
        labels = loadLabels(context, labelsFilename)
    }

    private fun loadModelFile(context: Context, filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
    }

    private fun loadLabels(context: Context, filename: String): List<String> {
        return context.assets.open(filename).bufferedReader().useLines { it.toList() }
    }
}