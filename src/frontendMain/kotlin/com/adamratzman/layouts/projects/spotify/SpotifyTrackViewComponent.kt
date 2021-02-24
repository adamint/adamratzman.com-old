package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyArtistViewPage
import com.adamratzman.layouts.NotFoundComponent
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.setTitle
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpotifyTrackViewComponent(trackId: String, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom, TextCenter)) {
            GlobalScope.launch {
                api.tracks.getTrack(trackId)?.let { track ->
                    setTitle("View Spotify Track: ${track.name} by ${track.artists.joinToString(", ") { it.name }} ")

                    h2(classes = nameSetOf(MarginSmallBottom, "moderate-bold")) {
                        +"Track "
                        bold(content = track.name)
                    }
                    p(classes = nameSetOf(MarginRemoveTop, "light")) {
                        +"By "
                        track.artists.forEachIndexed { i, artist ->
                            link(label = artist.name, url = SpotifyArtistViewPage(artist.id).devOrProdUrl())
                            if (i != track.artists.lastIndex) +", "
                        }
                    }
                    image(src = track.album.images.firstOrNull()?.url, classes = nameSetOf(MarginAuto, "block")) {
                        style {
                            width = 300 to px
                            height = 300 to px
                        }
                    }
                    iframe(
                        src = "https://open.spotify.com/embed/track/${track.id}",
                        iframeWidth = 300,
                        iframeHeight = 80,
                        classes = nameSetOf(MarginAuto)
                    )
                    removeLoadingSpinner(state)
                } ?: NotFoundComponent(this@div)
            }
        }

    }
})