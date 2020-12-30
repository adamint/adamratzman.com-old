package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.SiteManager
import com.adamratzman.database.SiteState
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import pl.treksoft.kvision.core.Container

const val spotifyClientId = "4341dad364794fbaa97a37fd4739b088"
val spotifyRedirectUri = encodeURIComponent(SiteManager.domain)

val spotifyAuthRedirectUri = getSpotifyAuthorizationUrl(
        SpotifyScope.PLAYLIST_MODIFY_PUBLIC,
        SpotifyScope.USER_TOP_READ,
        clientId = spotifyClientId,
        redirectUri = spotifyRedirectUri,
        isImplicitGrantFlow = true
)

external fun encodeURIComponent(encodedURI: String): String
external fun decodeURIComponent(encodedURI: String): String

fun Container.guardValidSpotifyApi(state: SiteState, block: (SpotifyImplicitGrantApi) -> Unit) {
    println(state.spotifyImplicitGrantApi?.token)
    println(state.spotifyImplicitGrantApi?.token?.shouldRefresh())
    if (state.spotifyImplicitGrantApi?.token?.shouldRefresh() == false) block(state.spotifyImplicitGrantApi!!)
    else SiteManager.redirectToSpotifyAuthentication(this)
}
