package com.adamratzman

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.layouts.*
import com.adamratzman.layouts.partials.FooterComponent
import com.adamratzman.layouts.partials.HeaderComponent
import com.adamratzman.layouts.projects.*
import com.adamratzman.layouts.projects.conversion.BaseConversionComponent
import com.adamratzman.layouts.projects.spotify.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.Application
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
            HeaderComponent(this)

            main(SiteManager.siteStore) { state ->
                id = "main"
                println(state.view.name)
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
                }

            }

            FooterComponent(this)


            GlobalScope.launch {

            }
        }
    }
}

fun main() {
    startApplication(::App, module.hot)
}
