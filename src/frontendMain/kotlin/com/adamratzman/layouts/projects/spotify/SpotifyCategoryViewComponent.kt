package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyCategoryListPage
import com.adamratzman.layouts.NotFoundComponent
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.layouts.setTitle
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.isMobile
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import io.kvision.core.Container
import io.kvision.core.UNIT.px
import io.kvision.core.style
import io.kvision.html.*
import io.kvision.utils.Intl
import io.kvision.utils.perc
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class SpotifyCategoryViewComponent(categoryId: String, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        GlobalScope.launch {
            if (!::allCategories.isInitialized) allCategories = api.browse.getCategoryList().getAllItemsNotNull()

            if (categoryId !in allCategories.map { it.id }) {
                NotFoundComponent(parent)
                return@launch
            }

            val playlistsForCategory =
                api.browse.getPlaylistsForCategory(categoryId, limit = 10)
                    .map { simple -> async { simple?.toFullPlaylist()?.let { simple to it } } }
                    .awaitAll().filterNotNull()

            allCategories.find { it.id == categoryId }?.let { category ->
                setTitle("View Spotify Category: ${category.name}")
                div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
                    div(classes = nameSetOf(TextCenter, MarginMediumBottom)) {
                        h2(classes = nameSetOf(MarginSmallBottom, "moderate-bold")) {
                            link(label = "Category", url = SpotifyCategoryListPage.devOrProdUrl())
                            +" "
                            b(content = category.name, rich = true)
                        }

                        category.icons.firstOrNull()?.let { image ->
                            image(src = image.url, classes = nameSetOf(MarginAuto, MarginSmallBottom, "block")) {
                                style {
                                    image.width?.let { width = it to px }
                                    image.height?.let { height = it to px }
                                }
                            }
                        }

                        goBackToProjectHome()

                    }

                    div(classes = nameSetOf("width-1-2@m")) {
                        if (!isMobile()) style { marginLeft = 25.perc }
                        div(classes = nameSetOf(MarginMediumLeft)) {
                            h3(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                                b("Top playlists:")
                            }

                            playlistsForCategory.forEach { playlistPair ->
                                val simplePlaylist = playlistPair.first
                                val playlist = playlistPair.second
                                PlaylistPreviewComponent(
                                    simplePlaylist,
                                    this,
                                    bottomComponent = {
                                        span {
                                            +"From "
                                            link(
                                                label = playlist.owner.displayName ?: playlist.owner.id,
                                                url = "https://open.spotify.com/user/${playlist.owner.id}"
                                            )
                                            +". ${Intl.NumberFormat().format(playlist.followers.total ?: 0)} followers"
                                            +". ${playlist.tracks.total} total songs"
                                            playlist.description?.let { span(". $it", rich = true) }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } ?: NotFoundComponent(parent)

            removeLoadingSpinner(state)
        }
    }
})