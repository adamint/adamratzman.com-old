package com.adamratzman.utils

import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.CssSize
import pl.treksoft.kvision.form.text.AbstractText
import pl.treksoft.kvision.html.textNode

fun <T : AbstractText> T.withPlaceholderAndMaxWidth(maxWidth: CssSize, placeholder: String): T {
    input.apply {
        this.maxWidth = maxWidth
        this.placeholder = placeholder
    }
    return this
}

fun Container.addBootstrap() = textNode("""<link type="text/css" rel="stylesheet" href="/static/css/bootstrap.min.css" />""", rich = true)