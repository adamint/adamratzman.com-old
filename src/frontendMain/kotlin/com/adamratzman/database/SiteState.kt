package com.adamratzman.database

import com.adamratzman.database.SiteAction.*
import com.adamratzman.database.SiteManager.setSpotifyApi
import com.adamratzman.database.View.*
import com.adamratzman.layouts.logInClientSide
import com.adamratzman.models.*
import com.adamratzman.security.spotifyClientId
import com.adamratzman.services.ClientSideData
import com.adamratzman.services.SerializableDate
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.models.Token
import com.adamratzman.spotify.spotifyImplicitGrantApi
import com.adamratzman.spotify.utils.getCurrentTimeMs
import com.adamratzman.utils.toDevOrProdUrl
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.dom.get
import org.w3c.dom.set
import pl.treksoft.kvision.html.Div
import pl.treksoft.kvision.state.ObservableList
import pl.treksoft.kvision.state.observableListOf
import pl.treksoft.navigo.Navigo
import redux.RAction
import kotlin.js.RegExp

const val spotifyTokenLocalStorageKey = "spotifyToken"
const val spotifyTokenExpiryLocalStorageKey = "spotifyTokenExpirationMillis"
const val redirectBackToLocalStorageKey = "redirectBackTo"
const val clientSideDataLocalStorageKey = "clientSideData"
val isDevServer = window.location.host == "localhost:3000"

class NavbarPage(val name: String, val url: String, val icon: String? = null, val view: View? = null) {
    constructor(view: View, icon: String? = null) : this(view.name, view.baseUrl, icon, view)
}

// pages to their icons
val defaultAccessibleNavbarPages: List<NavbarPage> = listOf(
    NavbarPage(ProjectsHome),
    NavbarPage(Portfolio),
    NavbarPage(ViewBlogHomePage(listOf())),
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
            spotifyImplicitGrantApi(spotifyClientId, token) {
                refreshTokenProducer = { throw IllegalStateException("API Token has expired and cannot be refreshed.") }
            }
        }

    var loadingDiv: Div? = null
    var clientSideData
        get(): ClientSideData? = localStorage[clientSideDataLocalStorageKey]?.let {
            try {
                Json.decodeFromString<ClientSideData>(it)
            } catch (e: Exception) {
                localStorage.removeItem(clientSideDataLocalStorageKey)
                null
            }
        }
        set(data) {
            if (data == null) localStorage.removeItem(clientSideDataLocalStorageKey)
            else localStorage[clientSideDataLocalStorageKey] = Json.encodeToString(data)
        }

    fun isLoggedIn() = clientSideData != null
}

sealed class View(val name: String, val url: String, val needsInitialLoadingSpinner: Boolean = false) {
    object Home : View("Home", "/")
    object NotFound : View("404 Not Found", "/404")
    object ProjectsHome : View("Online Projects", "/projects")
    object Portfolio : View("Portfolio", "/portfolio")
    object ContactMe : View("Contact Me", "/contact")
    object FrenchLearningPage : View("French Resources", "/projects/french")
    object BaseConversionPage : View("Base Conversion Tool", "/projects/conversion/base-converter", needsInitialLoadingSpinner = true)
    object UrlShortenerHomePage : View("URL Shortener", "/projects/shortener")
    data class UrlShortenerViewSingleShortenedLink(val path: String) : View(
        "URL Shortener - /$path",
        "/projects/shortener/info/$path",
        needsInitialLoadingSpinner = true
    ) {
        companion object {
            val regExp = RegExp("/projects/shortener/info/(.+)")
        }
    }

    object UrlShortenerViewAllShortenedLinks : View("All Shortened URLs", "/projects/shortener/all", needsInitialLoadingSpinner = true)
    data class UrlShortenerRedirectToShortenedLink(val path: String) : View(
        "URL Shortener - Redirecting from $path",
        "/u/$path"
    ) {
        companion object {
            val regExp = RegExp("/u/(.+)")
        }
    }

    object ArbitraryPrecisionCalculatorPage : View("Calculator", "/projects/calculator", needsInitialLoadingSpinner = true)
    object SpotifyPlaylistGeneratorPage : View("Spotify Music Recommender", "/projects/spotify/recommend")
    object SpotifyGenreListPage : View("All Spotify Genres", "/projects/spotify/genres/list", needsInitialLoadingSpinner = true)
    data class SpotifyTrackViewPage(val trackId: String) :
        View("Track Details", "/projects/spotify/tracks-view/$trackId", needsInitialLoadingSpinner = true) {
        companion object {
            val regExp = RegExp("/projects/spotify/tracks-view/(.+)")
        }
    }

    data class SpotifyArtistViewPage(val artistId: String) :
        View("Artist Details", "/projects/spotify/artists-view/$artistId", needsInitialLoadingSpinner = true) {
        companion object {
            val regExp = RegExp("/projects/spotify/artists-view/(.+)")
        }
    }

    data class SpotifyCategoryViewPage(val categoryName: String) :
        View("View category: $categoryName", "/projects/spotify/categories-view/$categoryName", needsInitialLoadingSpinner = true) {
        companion object {
            val regExp = RegExp("/projects/spotify/categories-view/(.+)")
        }
    }

    object SpotifyCategoryListPage : View("Spotify Categories", "/projects/spotify/categories", needsInitialLoadingSpinner = true)
    object LoginPage : View("Log in", "/login")
    object ProfilePage : View("My Profile", "/me", needsInitialLoadingSpinner = true)
    object LogoutPage : View("Log out", "/logout")
    object RegisterPage : View("Register", "/register")
    object ViewAllDailySongsPage : View("Daily Songs", "/projects/daily-songs", needsInitialLoadingSpinner = true)
    data class ViewDailySongPage(val date: SerializableDate) :
        View(
            "Daily Song - ${date.monthNumber + 1}/${date.dayOfMonth}/${date.year}",
            "/projects/daily-songs/${date.year}/${date.monthNumber + 1}/${date.dayOfMonth}",
            needsInitialLoadingSpinner = true
        ) {
        companion object {
            val regExp = RegExp("/projects/daily-songs/(\\d+)/(\\d+)/(\\d+)")
        }
    }

    object MyTopTracksAndArtistsPage : View("My Spotify Top Tracks and Artists", "/projects/spotify/mytop", needsInitialLoadingSpinner = true)

    data class ViewBlogHomePage(val filterCategories: List<String>) :
        View(
            if (filterCategories.isEmpty()) "Blog" else "Blog - Categories ${filterCategories.joinToString(", ")}",
            "/blog",
            needsInitialLoadingSpinner = true
        ) {
        companion object {
            val regExp = RegExp("/projects/daily-songs/(\\d+)/(\\d+)/(\\d+)")
        }
    }

    data class ViewBlogPostPage(val id: String) :
        View(
            "Blog",
            "/blog/posts/$id",
            needsInitialLoadingSpinner = true
        ) {
        companion object {
            val regExp = RegExp("/blog/posts/(.+)")
        }
    }


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
    object LoadSpotifyPlaylistGeneratorPage : SiteAction()
    object LoadSpotifyGenreListPage : SiteAction()
    data class SetSpotifyApi(val token: Token) : SiteAction()
    data class LoadSpotifyTrackViewPage(val trackId: String) : SiteAction()
    data class LoadSpotifyArtistViewPage(val artistId: String) : SiteAction()
    data class LoadSpotifyCategoryViewPage(val genre: String) : SiteAction()
    object LoadSpotifyCategoryListPage : SiteAction()
    object LoadLoginPage : SiteAction()
    object LoadRegisterPage : SiteAction()
    object LoadProfilePage : SiteAction()
    object LoadViewAllDailySongsPage : SiteAction()
    data class LoadViewDailySongPage(val date: SerializableDate) : SiteAction()
    object LoadMyTopTracksAndArtistsPage : SiteAction()
    data class LoadBlogHomePage(val filterCategories: List<String>) : SiteAction()
    data class LoadBlogPostPage(val id: String) : SiteAction()

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
    LoadSpotifyPlaylistGeneratorPage -> state.copy(view = SpotifyPlaylistGeneratorPage)
    LoadSpotifyGenreListPage -> state.copy(view = SpotifyGenreListPage)
    is SetSpotifyApi -> {
        localStorage[spotifyTokenLocalStorageKey] = Json.encodeToString(action.token)
        localStorage[spotifyTokenExpiryLocalStorageKey] = action.token.expiresAt.toString()
        state
    }
    LoadSpotifyPlaylistGeneratorPage -> state.copy(view = SpotifyPlaylistGeneratorPage)
    is LoadSpotifyTrackViewPage -> state.copy(view = SpotifyTrackViewPage(action.trackId))
    is LoadSpotifyArtistViewPage -> state.copy(view = SpotifyArtistViewPage(action.artistId))
    is LoadSpotifyCategoryViewPage -> state.copy(view = SpotifyCategoryViewPage(action.genre))
    LoadSpotifyCategoryListPage -> state.copy(view = SpotifyCategoryListPage)
    LoadLoginPage -> state.copy(view = LoginPage)
    LoadRegisterPage -> state.copy(view = RegisterPage)
    LoadProfilePage -> state.copy(view = ProfilePage)
    LoadViewAllDailySongsPage -> state.copy(view = ViewAllDailySongsPage)
    is LoadViewDailySongPage -> state.copy(view = ViewDailySongPage(action.date))
    LoadMyTopTracksAndArtistsPage -> state.copy(view = MyTopTracksAndArtistsPage)
    is LoadBlogHomePage -> state.copy(view = ViewBlogHomePage(action.filterCategories))
    is LoadBlogPostPage -> state.copy(view = ViewBlogPostPage(action.id))
}

fun Navigo.initialize(): Navigo {
    println(window.location.pathname)
    return on(Home.url, { _ ->
        when {
            window.location.hash.startsWith("#access_token") -> {
                setSpotifyApi()
                SiteManager.redirectBack(defaultUrl = Home.url)
            }
            SiteManager.redirectBackUrl != null -> SiteManager.redirectBack(defaultUrl = Home.url)
            window.location.pathname.length <= 1 -> homePage()
            else -> notFoundPage()
        }
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
        .on(SpotifyPlaylistGeneratorPage.url, { _ -> spotifyPlaylistGeneratorPage() })
        .on(SpotifyGenreListPage.url, { _ -> spotifyGenreListPage() })
        .on(SpotifyCategoryViewPage.regExp, { genre -> CategoryViewPage(genre) })
        .on(SpotifyArtistViewPage.regExp, { artistId -> artistViewPage(artistId) })
        .on(SpotifyTrackViewPage.regExp, { trackId -> trackViewPage(trackId) })
        .on(SpotifyCategoryListPage.url, { _ -> spotifyCategoryListPage() })
        .on(LoginPage.url, { _ -> loginPage() })
        .on(RegisterPage.url, { _ -> registerPage() })
        .on(ProfilePage.url, { _ -> profilePage() })
        .on(ViewAllDailySongsPage.url, { _ -> viewAllDailySongsPage() })
        .on(ViewDailySongPage.regExp, { year, month, day -> viewDailySongPage(SerializableDate(year.toInt(), month.toInt(), day.toInt())) })
        .on("/loggedIn", { _ -> logInClientSide() })
        .on("/logout", { _ -> SiteManager.redirectToAuthentication(Div()) })
        .on(MyTopTracksAndArtistsPage.url, { _ -> myTopTracksAndArtistsPage() })
        .on("/blog", { _ -> blogHomePage() })
        .on(ViewBlogPostPage.regExp, { postId -> blogPostPage(postId) })
        .apply { notFound({ _ -> notFoundPage() }) }
}