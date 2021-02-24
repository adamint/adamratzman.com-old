package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.SiteManager.domain
import com.adamratzman.database.View.GenerateSpotifyClientTokenPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.security.requiredSpotifyScopes
import com.adamratzman.security.spotifyClientId
import com.adamratzman.spotify.*
import com.adamratzman.spotify.models.Token
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addBootstrap
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeAbcCheckbox
import com.adamratzman.utils.removeLoadingSpinner
import io.kvision.core.*
import io.kvision.core.UNIT.px
import io.kvision.form.check.checkBox
import io.kvision.html.*
import io.kvision.panel.flexPanel
import io.kvision.state.observableListOf
import io.kvision.toast.Toast
import io.kvision.toast.ToastOptions
import io.kvision.toast.ToastPosition.TOPRIGHT
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val generateTokenRedirectUri = domain + GenerateSpotifyClientTokenPage().devOrProdUrl()

private fun Container.getTokenInfo(scopes: List<SpotifyScope>, api: SpotifyClientApi) {
    +"This API token contains the following scopes: "
    scopes.forEachIndexed { i, scope ->
        b(scope.uri)
        if (i != requiredSpotifyScopes.lastIndex) +", "
    }
    +". It expires in "
    b((api.token.expiresIn / 60).toString())
    +" minutes."
}

private fun copyToken(token: Token) {
    window.navigator.clipboard.writeText(token.accessToken).then {
        Toast.info(
            "Your API token has been copied to the clipboard.",
            "Copied to clipboard",
            ToastOptions(
                positionClass = TOPRIGHT,
                closeButton = true
            )
        )
    }
}

class GenerateSpotifyTokenComponent(val code: String?, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    if (code == null) removeLoadingSpinner(state)

    guardValidSpotifyApi(state) { api ->
        addBootstrap()

        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom, PaddingRemoveBottom, TextCenter)) {
            h2(content = "Generate a Spotify OAuth Token", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
            p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
                goBackToProjectHome()
            }

            h4("Your current API access token")
            p {
                getTokenInfo(requiredSpotifyScopes.toList(), api)
            }

            button("Copy") {
                marginBottom = 25 to px

                onClick {
                    copyToken(api.token)
                }
            }

            val scopesRequested = observableListOf<SpotifyScope>()

            GlobalScope.launch {
                if (code != null) {
                    val generatedToken = spotifyClientPkceApi(
                        spotifyClientId,
                        generateTokenRedirectUri,
                        SpotifyUserAuthorization(authorizationCode = code, pkceCodeVerifier = state.codeVerifier)
                    ).build().token

                    h4("Generated token")
                    p {
                        getTokenInfo(generatedToken.scopes?.toList() ?: listOf(), api)
                    }
                    button("Copy") {
                        marginBottom = 25 to px

                        onClick {
                            copyToken(generatedToken)
                        }
                    }
                }

                h4("Generate a new access token")
                flexPanel(
                    FlexDirection.ROW, FlexWrap.WRAP, JustifyContent.FLEXSTART, AlignItems.CENTER,
                    spacing = 25,
                    classes = nameSetOf(WidthOneHalf, MarginAuto)
                ) {
                    SpotifyScope.values().forEach { scope ->
                        checkBox(label = scope.uri) {
                            removeAbcCheckbox()

                            onEvent {
                                change = {
                                    if (!value) scopesRequested -= scope
                                    else scopesRequested += scope
                                }
                            }
                        }
                    }
                }

                div(scopesRequested) {
                    marginTop = 20 to px

                    +"Your authorization url: "
                    val url = getPkceAuthorizationUrl(
                        *scopesRequested.toTypedArray(),
                        clientId = spotifyClientId,
                        redirectUri = generateTokenRedirectUri,
                        codeChallenge = getSpotifyPkceCodeChallenge(state.codeVerifier)
                    )

                    link(label = url, url = url)
                }

                removeLoadingSpinner(state)
            }
        }
    }
})
