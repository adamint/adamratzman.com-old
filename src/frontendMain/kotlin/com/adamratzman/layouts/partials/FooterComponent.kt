package com.adamratzman.layouts.partials

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.services.AuthenticationServiceFrontend
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.toDevOrProdUrl
import io.kvision.core.Col.GRAY
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.core.style
import io.kvision.html.b
import io.kvision.html.div
import io.kvision.html.link
import io.kvision.html.p
import io.kvision.remote.ServiceException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FooterComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    GlobalScope.launch {
        try {
            state.clientSideData = AuthenticationServiceFrontend.getClientSideData()
        } catch (exception: ServiceException) {
            state.clientSideData = null
        }
    }

    div(classes = nameSetOf(TextCenter.asString)) {
        div(classes = nameSetOf(MarginMediumBottom.asString)) {
            val links = listOf(
                "About" to "/",
                "Interactive Projects" to "/interactives",
                "Portfolio" to "/projects",
                "Contact" to "/contact"
            ).map { it.first to it.second.toDevOrProdUrl() }

            links.forEach { (name, url) ->
                link(label = "", url = url, classes = nameSetOf("black", MarginMediumRight)) {
                    b(content = name)
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

        div(classes = nameSetOf(MarginSmallTop.asString)) {
            p {
                +"Adam Ratzman - 2020"
            }
        }
    }
})