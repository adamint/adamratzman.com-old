package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyCategoryViewPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.spotify.models.SpotifyCategory
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addUikitAttributes
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h2
import io.kvision.html.h3
import io.kvision.html.link

lateinit var allCategories: List<SpotifyCategory>

class SpotifyCategoryListComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
            div(classes = nameSetOf(WidthTwoThirds, MarginAuto)) {
                GlobalScope.launch {
                    if (!::allCategories.isInitialized) allCategories = api.browse.getCategoryList().getAllItemsNotNull()
                    h2(content = "Spotify Category List", classes = nameSetOf(MarginRemoveBottom, "moderate-bold"))
                    goBackToProjectHome()

                    div(classes = nameSetOf(GridSmall, ChildWidthOneThird.medium, MarginMediumTop, MarginMediumBottom)) {
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

                    removeLoadingSpinner(state)
                }
            }
        }

    }
})