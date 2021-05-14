package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.View.SpotifyArtistViewPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.spotify.models.Artist
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

class ArtistPreviewComponent(artist: ArtistPreviewInfo, parent: Container, bottomComponent: (Container.() -> Unit)? = null, target: String? = null) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        div(classes = nameSetOf(MarginSmallBottom, GridCollapse)) {
            addUikitAttributes(UkGridAttribute)
            link(label = "", url = SpotifyArtistViewPage(artist.id).devOrProdUrl(), target = target) {
                image(src = artist.imageUrl, classes = nameSetOf("smallPreviewImg"))
            }
            div(classes = nameSetOf(MarginSmallLeft, MarginSmallTop, WidthExpand, "black")) {
                p(classes = nameSetOf("songTitle", "bold", MarginRemoveBottom)) {
                    style { fontSize = 20 to px; lineHeight = 1 to normal }
                    link(
                        label = artist.name,
                        url = SpotifyArtistViewPage(artist.id).devOrProdUrl(),
                        target = target,
                        classes = nameSetOf("link-color")
                    )
                }
                bottomComponent?.invoke(this)
            }
        }
    })

data class ArtistPreviewInfo(
    val name: String,
    val id: String,
    val imageUrl: String?
)

fun Artist.asArtistPreview() = ArtistPreviewInfo(name, id, images.firstOrNull()?.url)