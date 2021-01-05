@file:Suppress("UNCHECKED_CAST")

package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyArtistViewPage
import com.adamratzman.database.View.SpotifyCategoryViewPage
import com.adamratzman.layouts.NotFoundComponent
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.layouts.setTitle
import com.adamratzman.spotify.models.Album
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.Track
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import kotlinx.coroutines.*
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.utils.Intl
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
class SpotifyArtistViewComponent(artistId: String, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
            GlobalScope.launch {
                api.artists.getArtist(artistId)?.let { artist ->
                    setTitle("View Spotify Artist: ${artist.name}")
                    val data = listOf<Deferred<Any>>(
                        async { api.artists.getArtistAlbums(artist.id).getAllItemsNotNull() },
                        async { api.artists.getArtistTopTracks(artist.id) },
                        async { api.artists.getRelatedArtists(artist.id) }
                    ).awaitAll()

                    val totalAlbums = (data[0] as List<Album>).size
                    val topTracks = (data[1] as List<Track>).take(5)
                    val relatedArtists = (data[2] as List<Artist>).take(10)

                    div(classes = nameSetOf(TextCenter, MarginMediumBottom)) {
                        h2(classes = nameSetOf(MarginSmallBottom, "moderate-bold")) {
                            +"Artist "
                            bold(content = artist.name)
                        }
                        p(classes = nameSetOf(MarginRemoveTop, "light")) {
                            link(label = artist.uri.uri, url = artist.externalUrls.find { it.name == "spotify" }?.url)
                        }

                        image(src = artist.images.firstOrNull()?.url, classes = nameSetOf(MarginAuto, "block")) {
                            style {
                                width = 150 to px
                                height = 150 to px
                            }
                        }
                    }

                    div(classes = nameSetOf("margin-left-30@m", "width-1-2@m")) {
                        div(classes = nameSetOf(MarginMediumLeft)) {
                            h3(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                                bold("Popularity: ")
                                +"${artist.popularity}/100"
                            }

                            h3(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                                bold("Followers: ")
                                +Intl.NumberFormat().format(artist.followers.total)
                            }

                            h3(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                                bold("Total albums: ")
                                +totalAlbums.toString()
                            }

                            h3(classes = nameSetOf(MarginSmallTop, MarginRemoveBottom)) { bold("Associated genres:") }
                            p(classes = nameSetOf(MarginRemoveTop, MarginSmallBottom)) {
                                artist.genres.forEachIndexed { i, genre ->
                                    link(label = genre, url = SpotifyCategoryViewPage(genre).devOrProdUrl())
                                    if (i != artist.genres.lastIndex) +", "
                                }
                            }

                            h3(classes = nameSetOf(MarginSmallTop, MarginRemoveBottom)) { bold("Related artists:") }
                            p(classes = nameSetOf(MarginRemoveTop, MarginSmallBottom)) {
                                relatedArtists.forEachIndexed { i, relatedArtist ->
                                    link(label = relatedArtist.name, url = SpotifyArtistViewPage(relatedArtist.id).devOrProdUrl())
                                    if (i != relatedArtists.lastIndex) +", "
                                }
                            }

                            h3(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                                bold("Top ${topTracks.size} tracks: ")
                            }
                            topTracks.forEach { track ->
                                TrackPreviewComponent(track,
                                    this,
                                    bottomComponent = { span("Popularity: ${it.popularity}. Duration: ${it.durationMs.milliseconds}") }
                                )
                            }
                            removeLoadingSpinner(state)
                        }
                    }
                } ?: NotFoundComponent(this@div)
            }
        }

    }
})