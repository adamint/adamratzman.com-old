package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyCategoryListPage
import com.adamratzman.layouts.NotFoundComponent
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.utils.Intl

class SpotifyCategoryViewComponent(categoryId: String, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        GlobalScope.launch {
            if (!::allCategories.isInitialized) allCategories = api.browse.getCategoryList().getAllItemsNotNull()
            val playlistsForCategory =
                api.browse.getPlaylistsForCategory(categoryId, limit = 10)
                    .map { simple -> async { simple?.toFullPlaylist()?.let { simple to it } } }
                    .awaitAll().filterNotNull()

            allCategories.find { it.id == categoryId }?.let { category ->
                div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
                    div(classes = nameSetOf(TextCenter, MarginMediumBottom)) {
                        h2(classes = nameSetOf(MarginSmallBottom, "moderate-bold")) {
                            link(label = "Category", url = SpotifyCategoryListPage.devOrProdUrl())
                            +" "
                            bold(content = category.name, rich = true)
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

                    div(classes = nameSetOf("margin-left-30@m", "width-1-2@m")) {
                        div(classes = nameSetOf(MarginMediumLeft)) {
                            h3(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                                bold("Top playlists:")
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
                                            +". ${Intl.NumberFormat().format(playlist.followers.total)} followers"
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
        }
    }
})