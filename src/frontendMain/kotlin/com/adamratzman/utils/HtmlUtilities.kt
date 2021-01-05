package com.adamratzman.utils

import com.adamratzman.database.SiteState
import com.adamratzman.database.isDevServer
import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.asList
import org.w3c.dom.url.URLSearchParams
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
    .replace("url.adamratzman.com", "adamratzman.com")
    .replace("projects.adamratzman.com", "adamratzman.com")

fun getRandomColor() = Color.name(Col.values().random())

fun Container.textWithLinkedIcon(textBefore: String, link: String, iconName: String, ratio: Number) {
    +"$textBefore "
    link(label = "", url = link) {
        addAttributes("uk-icon" to "icon: $iconName; ratio: $ratio")
    }
}

fun Container.addLineBreak() {
    textNode("<br/>", rich = true)
}

fun Tag.noBorderRounding() {
    addCssClass("no-rounded-border")
}

fun Widget.unfocus() {
    hide()
    show()
}

fun removeLoadingSpinner(state: SiteState) {
    state.loadingDiv?.removeAll()
}

fun getSearchParams() = URLSearchParams(if (!window.location.search.isBlank()) window.location.search  else window.location.hash.substringAfter("?"))

/*
window.matchMedia("only screen and (max-width: 760px)").matches;
 */

fun isMobile() = window.matchMedia("only screen and (max-width: 760px)").matches

fun fixDropdownMobile() {
    document.getElementsByClassName("dropdown-item").asList().forEach { dropdownItem ->
        dropdownItem.removeAttribute("href")
    }
}