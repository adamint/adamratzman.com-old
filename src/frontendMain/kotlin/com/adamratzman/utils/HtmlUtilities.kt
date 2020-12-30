package com.adamratzman.utils

import com.adamratzman.database.isDevServer
import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.html.Tag
import pl.treksoft.kvision.html.link
import pl.treksoft.kvision.html.textNode

fun Component.addUikitAttributes(vararg attributes: Any) {
    attributes.forEach { setAttribute(it.toString(), "") }
}

fun Component.addAttributes(vararg attributePairs: Pair<Any, Any?>) {
    attributePairs.forEach { setAttribute(it.first.toString(), it.second?.toString() ?: "") }
}

fun Component.addCssClasses(vararg classes: Any) {
    classes.forEach { addCssClass(it.toString()) }
}

fun nameSetOf(vararg name: Any) = name.map { it.toString() }.toSet()

fun String.prependSpace() = " $this"

fun String.toDevOrProdUrl() = if (isDevServer) "/#!$this" else this

fun getRandomColor() = Color.name(Col.values().random())

fun Container.textWithLinkedIcon(textBefore: String, link: String, iconName: String, ratio: Number) {
    +"$textBefore "
    link(label = "", url = link) {
        addAttributes("uk-icon" to "icon: $iconName; ratio: $ratio")
    }
}

fun Container.addLineBreak() {
    textNode("<br/>", rich=true)
}

fun Tag.noBorderRounding() {
    addCssClass("no-rounded-border")
}