package com.adamratzman.layouts

import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addAttributes
import com.adamratzman.utils.getRandomColor
import com.adamratzman.utils.nameSetOf
import pl.treksoft.kvision.core.Background
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h1
import pl.treksoft.kvision.html.h4
import pl.treksoft.kvision.html.iframe

class NotFoundComponent(parent: Container) : SiteStatefulComponent(parent = parent,buildStatefulComponent = {
    setTitle("404 Not Found")

    div(classes = nameSetOf(UkSection, MarginXlargeLeft, MarginXlargeRight)) {
        div(className = TextCenter.asString) {
            style {
                background = Background(color = getRandomColor())
            }

            div(className = "white") {
                h1(classes = nameSetOf("super-bold", MarginSmallBottom)) {
                    +"Oh no, page not found!"
                }
                h4(className = "moderate-bold") {
                    +"Looks like you need some help"
                }

                iframe(src = "https://www.youtube.com/embed/dQw4w9WgXcQ", iframeWidth = 560,
                        iframeHeight = 315, className = MarginSmallBottom.asString) {
                    addAttributes("allow" to "autoplay; encrypted-media", "allowfullscreen" to "")
                }
            }

        }
    }

})