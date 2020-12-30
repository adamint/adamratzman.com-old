package com.adamratzman.layouts.projects

import com.adamratzman.utils.toDevOrProdUrl
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.link

fun Container.goBackToProjectHome() {
    +"Not what you're looking for? Check the "
    link(label = "projects", url = "/projects".toDevOrProdUrl())
    +" homepage"
}