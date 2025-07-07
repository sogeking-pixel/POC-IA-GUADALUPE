package com.example.poc_filtro_ia.ui


sealed interface UiState {

    /**
     * Empty state when the screen is first shown
     */
    object Initial : UiState

    /**
     * Still loading with tensorflow
     */
    object LoadingTensorFlow : UiState

    object Success : UiState

    /**
     * scale numeric value between 0 and 1
     */
    data class SuccessTensorFlow(val outputText: String, val outputScale: Float) : UiState


    /**
     * There was an error generating text
     */
    data class Error(val errorMessage: String) : UiState
}