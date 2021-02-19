package com.adamratzman.security

import com.adamratzman.database.SiteManager
import com.adamratzman.database.SiteState
import com.adamratzman.database.spotifyTokenExpiryLocalStorageKey
import com.adamratzman.database.spotifyTokenLocalStorageKey
import com.adamratzman.services.AuthenticationServiceFrontend
import com.adamratzman.services.ClientSideData
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.getSpotifyAuthorizationUrl
import com.adamratzman.spotify.utils.getCurrentTimeMs
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.get
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.remote.ServiceException

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
    localStorage[spotifyTokenExpiryLocalStorageKey]?.toLongOrNull()?.let {
        if (getCurrentTimeMs() >= it) {
            localStorage.removeItem(spotifyTokenLocalStorageKey)
            localStorage.removeItem(spotifyTokenExpiryLocalStorageKey)
            SiteManager.redirectToSpotifyAuthentication(this)
            return
        }
    }

    if (state.spotifyImplicitGrantApi?.token?.shouldRefresh() == false) block(state.spotifyImplicitGrantApi!!)
    else SiteManager.redirectToSpotifyAuthentication(this)
}

fun Container.guardLoggedIn(state: SiteState, block: (ClientSideData) -> Unit) {
    val clientSideDataInvalidationTime = state.clientSideDataInvalidationTime
    if (state.clientSideData != null && (clientSideDataInvalidationTime == null || clientSideDataInvalidationTime < getCurrentTimeMs())) {
        state.clientSideData!!.let(block)

    } else {
        GlobalScope.launch {
            try {
                state.clientSideData = AuthenticationServiceFrontend.getClientSideData()
                state.clientSideDataInvalidationTime = getCurrentTimeMs()
                state.clientSideData!!.let(block)
                window.location.href = window.location.href
            } catch (exception: ServiceException) {
                state.clientSideDataInvalidationTime = null
                SiteManager.redirectToAuthentication(this@guardLoggedIn)
            }
        }
    }
}