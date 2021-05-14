package com.adamratzman

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.layouts.*
import com.adamratzman.layouts.partials.FooterComponent
import com.adamratzman.layouts.partials.HeaderComponent
import com.adamratzman.layouts.projects.*
import com.adamratzman.layouts.projects.blog.BlogHomePage
import com.adamratzman.layouts.projects.blog.BlogPostPage
import com.adamratzman.layouts.projects.conversion.BaseConversionComponent
import com.adamratzman.layouts.projects.spotify.*
import com.adamratzman.layouts.user.ProfilePageComponent
import com.adamratzman.utils.UikitName.UkSpinnerAttribute
import com.adamratzman.utils.addAttributes
import com.adamratzman.utils.getSearchParams
import io.kvision.Application
import io.kvision.core.Position
import io.kvision.core.UNIT.perc
import io.kvision.core.UNIT.px
import io.kvision.core.style
import io.kvision.html.div
import io.kvision.html.main
import io.kvision.module
import io.kvision.panel.root
import io.kvision.routing.Routing
import io.kvision.startApplication

class App : Application() {
    init {
        io.kvision.require("static/css/main.css")
    }

    override fun start(state: Map<String, Any>) {
        Routing.init()
        SiteManager.initialize()
        root("kvapp", addRow = false) {
            if (getSearchParams().get("logout") != null) {
                SiteManager.redirectToAuthentication(this)
            }

            HeaderComponent(this)

            main(SiteManager.siteStore) { state ->
                id = "main"
                println(state.view.name)

                state.loadingDiv = div {
                    div {
                        if (!state.view.needsInitialLoadingSpinner) hide()
                        style {
                            right = 0 to px
                            position = Position.ABSOLUTE
                            marginRight = 10 to perc
                        }
                        addAttributes(UkSpinnerAttribute to "ratio: 2;")
                    }
                }

                when (state.view) {
                    Home -> HomePageComponent(this)
                    Portfolio -> PortfolioComponent(this)
                    NotFound -> NotFoundComponent(this)
                    ProjectsHome -> ProjectsHomeComponent(this)
                    ContactMe -> ContactMeComponent(this)
                    FrenchLearningPage -> FrenchLearningComponent(this)
                    BaseConversionPage -> BaseConversionComponent(this)
                    UrlShortenerHomePage -> UrlShortenerHomePageComponent(this)
                    is UrlShortenerViewSingleShortenedLink -> UrlShortenerViewSingleShortenedLinkComponent(this)
                    UrlShortenerViewAllShortenedLinks -> UrlShortenerViewAllShortenedLinksComponent(this)
                    is UrlShortenerRedirectToShortenedLink -> UrlShortenerRedirectToShortenedLinkComponent(this)
                    ArbitraryPrecisionCalculatorPage -> ArbitraryPrecisionCalculatorComponent(this)
                    SpotifyPlaylistGeneratorPage -> SpotifyPlaylistGeneratorComponent(this)
                    SpotifyGenreListPage -> SpotifyGenreListComponent(this)
                    is SpotifyTrackViewPage -> SpotifyTrackViewComponent(state.view.trackId, this)
                    is SpotifyCategoryViewPage -> SpotifyCategoryViewComponent(state.view.categoryName, this)
                    is SpotifyArtistViewPage -> SpotifyArtistViewComponent(state.view.artistId, this)
                    SpotifyCategoryListPage -> SpotifyCategoryListComponent(this)
                    LoginPage -> LoginComponent(this)
                    ProfilePage -> ProfilePageComponent(this)
                    LogoutPage -> throw NotImplementedError()
                    RegisterPage -> RegisterComponent(this)
                    ViewAllDailySongsPage -> ViewAllDailySongsComponent(this)
                    is ViewDailySongPage -> ViewDailySongComponent(state.view.date.copy(monthNumber = state.view.date.monthNumber - 1), this)
                    MyTopTracksAndArtistsPage -> MyTopTracksAndArtistsComponent(this)
                    is ViewBlogHomePage -> BlogHomePage(state.view.filterCategories, this)
                    is ViewBlogPostPage -> BlogPostPage(state.view.id, this)
                    is GenerateSpotifyClientTokenPage -> GenerateSpotifyTokenComponent(state.view.code, this)

                }
            }

            FooterComponent(this)
        }
    }
}

fun main() {
    startApplication(::App, module.hot)
}
