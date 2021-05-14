package com.adamratzman.utils

import io.kvision.jquery.JQueryAjaxSettings
import io.kvision.jquery.JQueryXHR

internal fun fixServiceRoutingWithOptionalBeforeSending(additionalBeforeSend: ((JQueryXHR, JQueryAjaxSettings) -> Boolean)? = null): ((JQueryXHR, JQueryAjaxSettings) -> Boolean) {
    return { jqueryXHR: JQueryXHR, jQueryAjaxSettings: JQueryAjaxSettings ->
        jQueryAjaxSettings.url = "/${jQueryAjaxSettings.url}"
        additionalBeforeSend?.invoke(jqueryXHR, jQueryAjaxSettings) ?: true
    }
}