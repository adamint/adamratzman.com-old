package com.adamratzman.database

import com.adamratzman.database.SiteAction.*
import com.adamratzman.layouts.projects.spotify.spotifyAuthRedirectUri
import com.adamratzman.spotify.utils.parseSpotifyCallbackHashToToken
import com.adamratzman.utils.toDevOrProdUrl
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set
import pl.treksoft.kvision.core.Col
import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.h2
import pl.treksoft.kvision.redux.createReduxStore
import pl.treksoft.kvision.routing.routing
import pl.treksoft.navigo.Navigo

object SiteManager {
    var redirectBackUrl: String?
        get() = localStorage[redirectBackToLocalStorageKey]
        set(value) = value?.let { localStorage[redirectBackToLocalStorageKey] = value } ?: localStorage.removeItem(redirectBackToLocalStorageKey)

    val domain = "${window.location.protocol}//${
        if (window.location.port in listOf("80", "443")) window.location.hostname
        else window.location.host
    }"

    val siteStore = createReduxStore(::siteStateReducer, SiteState())

    val navigo = Navigo(null, isDevServer, if (isDevServer) "#!" else null)

    fun stringParameter(params: dynamic, parameterName: String): String {
        return (params[parameterName]).toString()
    }

    fun initialize() {
        navigo.initialize().resolve()
        siteStore.dispatch(SiteLoaded)
    }

    fun replaceWithUrl(url: String) {
        window.location.replace(url.toDevOrProdUrl())
    }

    fun redirectToUrl(url: String) {
        window.location.href = url
    }

    fun redirect(view: View) {
        routing.navigate(view.url)
    }

    fun redirectBack(defaultUrl: String) {
        val redirectBackTo = redirectBackUrl
        redirectBackUrl = null
        window.location.href = redirectBackTo ?: defaultUrl.toDevOrProdUrl()
    }

    fun homePage() = siteStore.dispatch(LoadHomePage)
    fun portfolioPage() = siteStore.dispatch(LoadPortfolioPage)
    fun projectsHomePage() = siteStore.dispatch(LoadProjectsHomePage)
    fun notFoundPage() = siteStore.dispatch(LoadNotFoundPage)
    fun contactPage() = siteStore.dispatch(LoadContactMePage)
    fun frenchResourcesPage() = siteStore.dispatch(LoadFrenchLearningPage)
    fun baseConverterPage() = siteStore.dispatch(LoadBaseConversionPage)
    fun shortenerHomePage() = siteStore.dispatch(LoadUrlShortenerHomePage)
    fun shortenerViewShortenedLink(path: String) = siteStore.dispatch(LoadUrlShortenerViewSingleShortenedLink(path))
    fun shortenerViewAllShortenedLinks() = siteStore.dispatch(LoadUrlShortenerViewAllShortenedLinks)
    fun shortenerRedirectToShortenedLink(path: String) = siteStore.dispatch(LoadUrlShortenerRedirectToShortenedLink(path))
    fun calculatorPage() = siteStore.dispatch(LoadArbitraryPrecisionCalculatorPage)
    fun spotifyPlaylistGeneratorPage() = siteStore.dispatch(LoadSpotifyPlaylistGeneratorPage)
    fun spotifyGenreListPage() = siteStore.dispatch(LoadSpotifyGenreListPage)
    fun CategoryViewPage(genre: String) = siteStore.dispatch(LoadSpotifyCategoryViewPage(genre))
    fun artistViewPage(artistId: String) = siteStore.dispatch(LoadSpotifyArtistViewPage(artistId))
    fun trackViewPage(trackId: String) = siteStore.dispatch(LoadSpotifyTrackViewPage(trackId))
    fun spotifyCategoryListPage() = siteStore.dispatch(LoadSpotifyCategoryListPage)

    fun redirectToSpotifyAuthentication(parent: Container) {
        try {
            redirectBackUrl = window.location.href
            window.location.href = spotifyAuthRedirectUri
        } catch (exception: Exception) {
            parent.h2 {
                style { color = Color.name(Col.RED) }
                +"Your browser does not support localStorage. Please exit incognito mode."
            }
        }
    }

    fun setSpotifyApi() {
        siteStore.dispatch(SetSpotifyApi(parseSpotifyCallbackHashToToken()))
    }
}