package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyCategoryViewPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.spotify.models.SpotifyCategory
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addUikitAttributes
import com.adamratzman.utils.nameSetOf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h2
import pl.treksoft.kvision.html.h3
import pl.treksoft.kvision.html.link

lateinit var allCategories: List<SpotifyCategory>

class SpotifyCategoryListComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
            div(classes = nameSetOf(WidthTwoThirds, MarginAuto)) {
                GlobalScope.launch {
                    if (!::allCategories.isInitialized) allCategories = api.browse.getCategoryList().getAllItemsNotNull()
                    h2(content = "Spotify Category List", classes = nameSetOf(MarginRemoveBottom, "moderate-bold"))
                    goBackToProjectHome()

                    div(classes = nameSetOf(GridSmall, ChildWidthOneThird, MarginMediumTop, MarginMediumBottom)) {
                        addUikitAttributes(UkGridAttribute)

                        allCategories.forEach { category ->
                            div {
                                div(classes = nameSetOf(UkCard, UkCardDefault, UkCardBody)) {
                                    h3(className = UkCardTitle.asString) {
                                        link(label = category.name, url = SpotifyCategoryViewPage(category.id).devOrProdUrl())
                                    }

                                    category.icons.forEach { image ->
                                        link(label = "", image = image.url, url = SpotifyCategoryViewPage(category.id).devOrProdUrl())
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

    }
})