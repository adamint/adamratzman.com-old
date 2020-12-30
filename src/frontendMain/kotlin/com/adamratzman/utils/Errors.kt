package com.adamratzman.utils

import pl.treksoft.kvision.remote.ServiceException
import pl.treksoft.kvision.toast.Toast
import pl.treksoft.kvision.toast.ToastOptions
import pl.treksoft.kvision.toast.ToastPosition.TOPRIGHT

fun Exception.getErrorMessage() = message ?: "There was an error processing your request."

fun ServiceException.showDefaultErrorToast(title: String = "Error") {
    Toast.error(
            getErrorMessage(),
            title,
            ToastOptions(
                    positionClass = TOPRIGHT,
                    closeButton = true
            )
    )
}