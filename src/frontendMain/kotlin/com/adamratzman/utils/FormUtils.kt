package com.adamratzman.utils

import io.kvision.core.Container
import io.kvision.core.CssSize
import io.kvision.form.text.AbstractText
import io.kvision.html.textNode

fun <T : AbstractText> T.withPlaceholderAndMaxWidth(maxWidth: CssSize, placeholder: String): T {
    input.apply {
        this.maxWidth = maxWidth
        this.placeholder = placeholder
    }
    return this
}

fun Container.addBootstrap() = textNode("""<link type="text/css" rel="stylesheet" href="/static/css/bootstrap.min.css" />""", rich = true)