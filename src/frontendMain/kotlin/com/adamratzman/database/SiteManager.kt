package com.adamratzman.database

import com.adamratzman.database.SiteAction.*
import com.adamratzman.database.View.LoginPage
import com.adamratzman.security.spotifyAuthRedirectUri
import com.adamratzman.services.SerializableDate
import com.adamratzman.spotify.utils.parseSpotifyCallbackHashToToken
import com.adamratzman.utils.getSearchParams
import com.adamratzman.utils.toDevOrProdUrl
import pl.treksoft.kvision.core.Col
import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.h2
import pl.treksoft.navigo.Navigo
import pl.treksoft.kvision.redux.createReduxStore
import pl.treksoft.kvision.routing.routing
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.get
import org.w3c.dom.set

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

    fun redirectToAuthentication(parent: Container) {
        try {
            siteStore.getState().clientSideData = null
            redirectBackUrl = window.location.href
            redirectToUrl(LoginPage.devOrProdUrl())
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

fun homePage() = SiteManager.siteStore.dispatch(LoadHomePage)
fun portfolioPage() = SiteManager.siteStore.dispatch(LoadPortfolioPage)
fun projectsHomePage() = SiteManager.siteStore.dispatch(LoadProjectsHomePage)
fun notFoundPage() = SiteManager.siteStore.dispatch(LoadNotFoundPage)
fun contactPage() = SiteManager.siteStore.dispatch(LoadContactMePage)
fun frenchResourcesPage() = SiteManager.siteStore.dispatch(LoadFrenchLearningPage)
fun baseConverterPage() = SiteManager.siteStore.dispatch(LoadBaseConversionPage)
fun shortenerHomePage() = SiteManager.siteStore.dispatch(LoadUrlShortenerHomePage)
fun shortenerViewShortenedLink(path: String) = SiteManager.siteStore.dispatch(LoadUrlShortenerViewSingleShortenedLink(path))
fun shortenerViewAllShortenedLinks() = SiteManager.siteStore.dispatch(LoadUrlShortenerViewAllShortenedLinks)
fun shortenerRedirectToShortenedLink(path: String) = SiteManager.siteStore.dispatch(LoadUrlShortenerRedirectToShortenedLink(path))
fun calculatorPage() = SiteManager.siteStore.dispatch(LoadArbitraryPrecisionCalculatorPage)
fun spotifyPlaylistGeneratorPage() = SiteManager.siteStore.dispatch(LoadSpotifyPlaylistGeneratorPage)
fun spotifyGenreListPage() = SiteManager.siteStore.dispatch(LoadSpotifyGenreListPage)
fun CategoryViewPage(genre: String) = SiteManager.siteStore.dispatch(LoadSpotifyCategoryViewPage(genre))
fun artistViewPage(artistId: String) = SiteManager.siteStore.dispatch(LoadSpotifyArtistViewPage(artistId))
fun trackViewPage(trackId: String) = SiteManager.siteStore.dispatch(LoadSpotifyTrackViewPage(trackId))
fun spotifyCategoryListPage() = SiteManager.siteStore.dispatch(LoadSpotifyCategoryListPage)
fun loginPage() = SiteManager.siteStore.dispatch(LoadLoginPage)
fun registerPage() = SiteManager.siteStore.dispatch(LoadRegisterPage)
fun profilePage() = SiteManager.siteStore.dispatch(LoadProfilePage)
fun viewAllDailySongsPage() = SiteManager.siteStore.dispatch(LoadViewAllDailySongsPage)
fun viewDailySongPage(date: SerializableDate) = SiteManager.siteStore.dispatch(LoadViewDailySongPage(date))
fun myTopTracksAndArtistsPage() = SiteManager.siteStore.dispatch(LoadMyTopTracksAndArtistsPage)
fun blogHomePage() = SiteManager.siteStore.dispatch(LoadBlogHomePage(getSearchParams().get("category")?.split(",") ?: listOf()))
fun blogPostPage(postId: String) = SiteManager.siteStore.dispatch(LoadBlogPostPage(postId))
fun generateSpotifyClientTokenPage() = SiteManager.siteStore.dispatch(LoadGenerateSpotifyClientTokenPage(getSearchParams().get("code")))