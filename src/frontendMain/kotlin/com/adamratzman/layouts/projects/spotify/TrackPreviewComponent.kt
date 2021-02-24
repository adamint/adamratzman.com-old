package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyTrackViewPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.Track
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addUikitAttributes
import com.adamratzman.utils.nameSetOf
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.normal
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.image
import pl.treksoft.kvision.html.link
import pl.treksoft.kvision.html.p

class TrackPreviewComponent(track: TrackPreviewInfo, parent: Container, bottomComponent: (Container.() -> Unit)? = null, target: String? = null) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        div(classes = nameSetOf(MarginSmallBottom, GridCollapse)) {
            addUikitAttributes(UkGridAttribute)
            link(label = "", url = SpotifyTrackViewPage(track.id).devOrProdUrl(), target = target) {
                image(src = track.album.images.first().url, classes = nameSetOf("smallPreviewImg"))
            }
            div(classes = nameSetOf(MarginSmallLeft, MarginSmallTop, WidthExpand, "black")) {
                p(classes = nameSetOf("songTitle", "bold", MarginRemoveBottom)) {
                    style { fontSize = 20 to px; lineHeight = 1 to normal }
                    link(label = track.name, url = SpotifyTrackViewPage(track.id).devOrProdUrl(), target = target, classes = nameSetOf("link-color"))
                }
                bottomComponent?.invoke(this)
            }
        }
    })

data class TrackPreviewInfo(
    val name: String,
    val id: String,
    val album: SimpleAlbum
)

fun Track.asTrackPreview() = TrackPreviewInfo(name, id, album)