package com.adamratzman.database

import com.adamratzman.database.SiteAction.*
import com.adamratzman.database.SiteManager.baseConverterPage
import com.adamratzman.database.SiteManager.calculatorPage
import com.adamratzman.database.SiteManager.contactPage
import com.adamratzman.database.SiteManager.frenchResourcesPage
import com.adamratzman.database.SiteManager.homePage
import com.adamratzman.database.SiteManager.notFoundPage
import com.adamratzman.database.SiteManager.portfolioPage
import com.adamratzman.database.SiteManager.projectsHomePage
import com.adamratzman.database.SiteManager.setSpotifyApi
import com.adamratzman.database.SiteManager.shortenerHomePage
import com.adamratzman.database.SiteManager.shortenerRedirectToShortenedLink
import com.adamratzman.database.SiteManager.shortenerViewAllShortenedLinks
import com.adamratzman.database.SiteManager.shortenerViewShortenedLink
import com.adamratzman.database.SiteManager.spotifyGenreListPage
import com.adamratzman.database.SiteManager.spotifyRecommenderPage
import com.adamratzman.database.SiteManager.spotifyRecommenderUriHelpPage
import com.adamratzman.database.View.*
import com.adamratzman.layouts.projects.spotify.spotifyClientId
import com.adamratzman.layouts.projects.spotify.spotifyRedirectUri
import com.adamratzman.models.*
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyImplicitGrantApi
import com.adamratzman.spotify.utils.getCurrentTimeMs
import com.adamratzman.utils.toDevOrProdUrl
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import pl.treksoft.kvision.state.ObservableList
import pl.treksoft.kvision.state.observableListOf
import pl.treksoft.navigo.Navigo
import redux.RAction
import kotlin.js.RegExp

const val spotifyTokenLocalStorageKey = "spotifyToken"
const val spotifyTokenExpiryLocalStorageKey = "spotifyTokenExpirationMillis"
const val redirectBackToLocalStorageKey = "redirectBackTo"
val isDevServer = window.location.host == "localhost:3000"

class NavbarPage(val name: String, val url: String, val icon: String? = null, val view: View? = null) {
    constructor(view: View, icon: String? = null) : this(view.name, view.baseUrl, icon, view)
}

// pages to their icons
val defaultAccessibleNavbarPages: List<NavbarPage> = listOf(
        NavbarPage(Home),
        NavbarPage(ProjectsHome),
        NavbarPage(Portfolio),
        NavbarPage("Resume", "/static/files/resume.pdf"),
        NavbarPage("Github", "https://github.com/adamint", "/static/icons/mbr/mbri-github.svg"),
        NavbarPage(ContactMe, "/static/icons/mbr/mbri-paper-plane.svg")
)

data class SiteState(
        val loading: Boolean = true,
        val view: View = Home,
        val accessibleNavbarPages: ObservableList<NavbarPage> = observableListOf(*defaultAccessibleNavbarPages.toTypedArray()),
        val redirectAfterCallbackUri: String? = null
) {
    val currentProjects = getPresentProjects()
    val pastProjects = getPastProjects()
    val interactives = getInteractives()
    val educationExperience = getEducationExperience()
    val workExperience = getWorkExperience()
    val selectedProjects = getSelectedProjects()
    val studentInvolvement = getStudentInvolvement()

    val spotifyImplicitGrantApi
        get(): SpotifyImplicitGrantApi? = localStorage[spotifyTokenLocalStorageKey]?.let { tokenString ->
            localStorage[spotifyTokenExpiryLocalStorageKey]?.toLongOrNull()?.let {
                if (getCurrentTimeMs() >= it) {
                    localStorage.removeItem(spotifyTokenLocalStorageKey)
                    localStorage.removeItem(spotifyTokenExpiryLocalStorageKey)
                    return null
                }
            }
            val token = Json.decodeFromString(Token.serializer(), tokenString)
            spotifyImplicitGrantApi(spotifyClientId, spotifyRedirectUri, token)
        }
}

sealed class View(val name: String, val url: String) {
    object Home : View("Home", "/")
    object NotFound : View("404 Not Found", "/404")
    object ProjectsHome : View("Online Projects", "/projects")
    object Portfolio : View("Portfolio", "/portfolio")
    object ContactMe : View("Contact Me", "/contact")
    object FrenchLearningPage : View("French Resources", "/projects/french")
    object BaseConversionPage : View("Base Conversion Tool", "/projects/conversion/base-converter")
    object UrlShortenerHomePage : View("URL Shortener", "/projects/shortener")
    data class UrlShortenerViewSingleShortenedLink(val path: String) : View(
            "URL Shortener - /$path",
            "/projects/shortener/info/$path"
    ) {
        companion object {
            val regExp = RegExp("/projects/shortener/info/(.+)")
        }
    }

    object UrlShortenerViewAllShortenedLinks : View("All Shortened URLs", "/projects/shortener/all")
    data class UrlShortenerRedirectToShortenedLink(val path: String) : View(
            "URL Shortener - Redirecting from $path",
            "/u/$path"
    ) {
        companion object {
            val regExp = RegExp("/u/(.+)")
        }
    }

    object ArbitraryPrecisionCalculatorPage : View("Calculator", "/projects/calculator")
    object SpotifyRecommenderPage : View("Spotify Music Recommender", "/projects/spotify/recommender")
    object SpotifyRecommenderUriHelpPage : View("How to find Spotify URIs", "${SpotifyRecommenderPage.url}/uris")
    object SpotifyGenreListPage : View("All Spotify Genres", "/projects/spotify/genres/list")

    fun devOrProdUrl() = url.toDevOrProdUrl()
    fun isSameView(other: View) = this::class == other::class
    val baseUrl: String = url.toDevOrProdUrl()
}

sealed class SiteAction : RAction {
    object SiteLoaded : SiteAction()

    object LoadHomePage : SiteAction()
    object LoadPortfolioPage : SiteAction()
    object LoadProjectsHomePage : SiteAction()
    object LoadNotFoundPage : SiteAction()
    object LoadContactMePage : SiteAction()
    object LoadFrenchLearningPage : SiteAction()
    object LoadBaseConversionPage : SiteAction()
    object LoadUrlShortenerHomePage : SiteAction()
    data class LoadUrlShortenerViewSingleShortenedLink(val path: String) : SiteAction()
    object LoadUrlShortenerViewAllShortenedLinks : SiteAction()
    data class LoadUrlShortenerRedirectToShortenedLink(val path: String) : SiteAction()
    object LoadArbitraryPrecisionCalculatorPage : SiteAction()
    object LoadSpotifyRecommenderPage : SiteAction()
    object LoadSpotifyRecommenderUriHelpPage : SiteAction()
    object LoadSpotifyGenreListPage : SiteAction()
    data class SetSpotifyApi(val token: Token) : SiteAction()
}

fun siteStateReducer(state: SiteState, action: SiteAction): SiteState = when (action) {
    SiteLoaded -> state.copy(loading = false)
    LoadHomePage -> state.copy(view = Home)
    LoadPortfolioPage -> state.copy(view = Portfolio)
    LoadProjectsHomePage -> state.copy(view = ProjectsHome)
    LoadNotFoundPage -> state.copy(view = NotFound)
    LoadContactMePage -> state.copy(view = ContactMe)
    LoadFrenchLearningPage -> state.copy(view = FrenchLearningPage)
    LoadBaseConversionPage -> state.copy(view = BaseConversionPage)
    LoadUrlShortenerHomePage -> state.copy(view = UrlShortenerHomePage)
    is LoadUrlShortenerViewSingleShortenedLink -> state.copy(view = UrlShortenerViewSingleShortenedLink(action.path))
    LoadUrlShortenerViewAllShortenedLinks -> state.copy(view = UrlShortenerViewAllShortenedLinks)
    is LoadUrlShortenerRedirectToShortenedLink -> state.copy(view = UrlShortenerRedirectToShortenedLink(action.path))
    LoadArbitraryPrecisionCalculatorPage -> state.copy(view = ArbitraryPrecisionCalculatorPage)
    LoadSpotifyRecommenderPage -> state.copy(view = SpotifyRecommenderPage)
    LoadSpotifyRecommenderUriHelpPage -> state.copy(view = SpotifyRecommenderUriHelpPage)
    LoadSpotifyGenreListPage -> state.copy(view = SpotifyGenreListPage)
    is SetSpotifyApi -> {
        localStorage[spotifyTokenLocalStorageKey] = Json.encodeToString(action.token)
        localStorage[spotifyTokenExpiryLocalStorageKey] = action.token.expiresAt.toString()
        state
    }
}

fun Navigo.initialize(): Navigo {
    return on(Home.url, { _ ->
        if (window.location.hash.startsWith("#access_token")) {
            setSpotifyApi()
            SiteManager.redirectBack(defaultUrl = Home.url)
        } else homePage()
    })
            .on(Portfolio.url, { _ -> portfolioPage() })
            .on("/interactives", { _ -> SiteManager.replaceWithUrl("/projects") })
            .on(ContactMe.url, { _ -> contactPage() })
            .on(ProjectsHome.url, { _ -> projectsHomePage() })
            .on(FrenchLearningPage.url, { _ -> frenchResourcesPage() })
            .on("/utils/convert/base", { _ -> SiteManager.replaceWithUrl("/projects/conversion/base-converter") })
            .on(BaseConversionPage.url, { _ -> baseConverterPage() })
            .on("/shortener", { _ -> SiteManager.replaceWithUrl("/projects/shortener") })
            .on(UrlShortenerHomePage.url, { _ -> shortenerHomePage() })
            .on(UrlShortenerViewAllShortenedLinks.url, { _ -> shortenerViewAllShortenedLinks() })
            .on(UrlShortenerViewSingleShortenedLink.regExp, { path -> shortenerViewShortenedLink(path) })
            .on(UrlShortenerRedirectToShortenedLink.regExp, { path -> shortenerRedirectToShortenedLink(path) })
            .on(ArbitraryPrecisionCalculatorPage.url, { _ -> calculatorPage() })
            .on(SpotifyRecommenderPage.url, { _ -> spotifyRecommenderPage() })
            .on(SpotifyRecommenderUriHelpPage.url, { _ -> spotifyRecommenderUriHelpPage() })
            .on(SpotifyGenreListPage.url, { _ -> spotifyGenreListPage() })
            .apply { notFound({ _ -> notFoundPage() }) }
}