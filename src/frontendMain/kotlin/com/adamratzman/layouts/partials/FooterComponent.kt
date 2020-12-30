package com.adamratzman.layouts.partials

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.toDevOrProdUrl
import pl.treksoft.kvision.core.Col.GRAY
import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.bold
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.link
import pl.treksoft.kvision.html.p

class FooterComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = {
    div(className = TextCenter.asString) {
        div(className = MarginMediumBottom.asString) {
            val links = listOf(
                    "About" to "/",
                    "Interactive Projects" to "/interactives",
                    "Portfolio" to "/projects",
                    "Contact" to "/contact"
            ).map { it.first to it.second.toDevOrProdUrl() }

            links.forEach { (name, url) ->
                link(label = "", url = url, classes = nameSetOf("black", MarginMediumRight)) {
                    bold(content = name)
                }
            }
        }

        div {
            val iconRedirectMappings = listOf(
                    "https://linkedin.com/in/aratzman" to "socicon-linkedin",
                    "https://github.com/adamint" to "socicon-github",
                    "mailto:adam@adamratzman.com" to "socicon-mail"
            )

            iconRedirectMappings.forEachIndexed { index, (url, iconName) ->
                link(label = "", url = url, target = "_blank", classes = nameSetOf(iconName, "rem-1-5")) {
                    style {
                        this.color = Color.name(GRAY)
                    }
                    if (index != iconRedirectMappings.lastIndex) addCssClass("uk-margin-small-right")
                }
            }
        }

        div(className=MarginSmallTop.asString) {
            p {
                +"Adam Ratzman - 2020"
            }
        }
    }
})