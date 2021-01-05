package com.adamratzman

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.layouts.*
import com.adamratzman.layouts.partials.FooterComponent
import com.adamratzman.layouts.partials.HeaderComponent
import com.adamratzman.layouts.projects.*
import com.adamratzman.layouts.projects.conversion.BaseConversionComponent
import com.adamratzman.layouts.projects.spotify.*
import com.adamratzman.layouts.user.ProfilePageComponent
import com.adamratzman.utils.UikitName.UkSpinnerAttribute
import com.adamratzman.utils.addAttributes
import com.adamratzman.utils.getSearchParams
import pl.treksoft.kvision.Application
import pl.treksoft.kvision.core.Position
import pl.treksoft.kvision.core.UNIT.perc
import pl.treksoft.kvision.core.UNIT.px
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.main
import pl.treksoft.kvision.module
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.startApplication

class App : Application() {
    init {
        pl.treksoft.kvision.require("static/css/main.css")
    }

    override fun start(state: Map<String, Any>) {
        SiteManager.initialize()
        root("kvapp", addRow = false) {
            if (getSearchParams().get("logout") != null) {
                SiteManager.redirectToAuthentication(this)
            }

            HeaderComponent(this)

            main(SiteManager.siteStore) { state ->
                id = "main"
                println(state.view.name)

                if (state.view.needsInitialLoadingSpinner) {
                    state.loadingDiv = div {
                        div {
                            style {
                                right = 0 to px
                                position = Position.ABSOLUTE
                                marginRight = 10 to perc
                            }
                            addAttributes(UkSpinnerAttribute to "ratio: 2;")
                        }
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
                }

            }

            FooterComponent(this)
        }
    }
}

fun main() {
    startApplication(::App, module.hot)
}
