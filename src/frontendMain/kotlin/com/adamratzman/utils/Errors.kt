package com.adamratzman.utils

import io.kvision.remote.ServiceException
import io.kvision.toast.Toast
import io.kvision.toast.ToastOptions
import io.kvision.toast.ToastPosition.TOPRIGHT

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