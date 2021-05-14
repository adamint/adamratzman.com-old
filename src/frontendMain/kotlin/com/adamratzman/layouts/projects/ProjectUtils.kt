package com.adamratzman.layouts.projects

import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.toDevOrProdUrl
import io.kvision.core.Container
import io.kvision.html.link

fun Container.goBackToProjectHome() {
    +"Not what you're looking for? Check the "
    link(label = "projects", url = "/projects".toDevOrProdUrl(), classes = nameSetOf("link-color"))
    +" homepage"
}