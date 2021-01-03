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
    div(classes = nameSetOf(UkSection, WidthTwoThirds, MarginAuto)) {
        div(classes = nameSetOf(TextCenter.asString)) {
            style {
                background = Background(color = getRandomColor())
            }

            div(classes = nameSetOf("white")) {
                h1(classes = nameSetOf("super-bold", MarginSmallBottom)) {
                    +"Oh no, page not found!"
                }
                h4(classes = nameSetOf("moderate-bold")) {
                    +"Looks like you need some help"
                }

                iframe(src = "https://www.youtube.com/embed/dQw4w9WgXcQ", iframeHeight = 200, classes = nameSetOf(MarginSmallBottom.asString)) {
                    addAttributes("allow" to "autoplay; encrypted-media", "allowfullscreen" to "")
                }
            }

        }
    }

})