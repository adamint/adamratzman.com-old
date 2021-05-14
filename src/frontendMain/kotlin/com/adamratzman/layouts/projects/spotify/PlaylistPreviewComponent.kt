package com.adamratzman.layouts.projects.spotify

import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addUikitAttributes
import com.adamratzman.utils.nameSetOf
import io.kvision.core.Container
import io.kvision.core.UNIT.normal
import io.kvision.core.UNIT.px
import io.kvision.core.style
import io.kvision.html.div
import io.kvision.html.image
import io.kvision.html.link
import io.kvision.html.p

class PlaylistPreviewComponent(
    playlist: SimplePlaylist,
    parent: Container,
    bottomComponent: (Container.(SimplePlaylist) -> Unit)? = null,
    target: String? = null
) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        div(classes = nameSetOf(MarginSmallBottom, GridCollapse)) {
            addUikitAttributes(UkGridAttribute)
            val url = playlist.externalUrls.find { it.name == "spotify" }?.url
            link(label = "", url = url, target = target) {
                image(src = playlist.images.first().url, classes = nameSetOf("smallPreviewImg"))
            }
            div(classes = nameSetOf(MarginSmallLeft, MarginSmallTop, WidthExpand, "black")) {
                p(classes = nameSetOf("songTitle", "bold", MarginRemoveBottom)) {
                    style { fontSize = 20 to px; lineHeight = 1 to normal }
                    link(label = playlist.name, url = url, target = target, classes = nameSetOf("link-color"))
                }
                bottomComponent?.invoke(this, playlist)
            }
        }
    })