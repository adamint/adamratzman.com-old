package com.adamratzman.utils

import pl.treksoft.jquery.JQueryAjaxSettings
import pl.treksoft.jquery.JQueryXHR

internal fun fixServiceRoutingWithOptionalBeforeSending(additionalBeforeSend: ((JQueryXHR, JQueryAjaxSettings) -> Boolean)? = null): ((JQueryXHR, JQueryAjaxSettings) -> Boolean) {
    return { jqueryXHR: JQueryXHR, jQueryAjaxSettings: JQueryAjaxSettings ->
        jQueryAjaxSettings.url = "/${jQueryAjaxSettings.url}"
        additionalBeforeSend?.invoke(jqueryXHR, jQueryAjaxSettings) ?: true
    }
}