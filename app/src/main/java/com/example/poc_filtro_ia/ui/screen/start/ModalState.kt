package com.example.poc_filtro_ia.ui.screen.start

sealed class AlertType {
    object Success : AlertType()
    object Danger : AlertType()
    object Warning : AlertType()
}

data class ModalState(
    val show: Boolean = false,
    val type: AlertType? = null,
    val message: String = "",
    val onDismiss: () -> Unit = {}
)