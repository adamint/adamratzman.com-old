package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.SiteState
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.services.BaseConverterServiceFrontend
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.endpoints.client.ClientPersonalizationApi.TimeRange
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.onEvent
import pl.treksoft.kvision.form.form
import pl.treksoft.kvision.form.select.select
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.panel.tab
import pl.treksoft.kvision.panel.tabPanel
import pl.treksoft.kvision.tabulator.*
import pl.treksoft.kvision.utils.px
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

class MyTopTracksAndArtistsComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        div(classes = nameSetOf(MarginMediumTop, PaddingRemoveBottom)) {
            h2(content = "Your Top Spotify Tracks and Artists", classes = nameSetOf(MarginRemoveBottom, TextCenter, "moderate-bold"))
            p(classes = nameSetOf(MarginSmallTop, MarginMediumBottom, TextCenter, "light")) {
                goBackToProjectHome()
            }

            div(classes = nameSetOf(MarginAuto, MarginSmallBottom, if (isMobile()) WidthOneOne else WidthTwoThirds)) {
                div {
                    addBootstrap()
                    GlobalScope.launch {
                        delay(2000)
                        @Suppress("JoinDeclarationAndAssignment")
                        lateinit var outputDiv: Div
                        form(classes = nameSetOf(MarginMediumBottom.asString)) {
                            select(
                                // value = TimeRange.SHORT_TERM.toReadable(),
                                label = "Time Range:",
                                options = TimeRange.values().reversed().map { it.id to it.toReadable() }
                            ) {
                                onEvent {
                                    change = {
                                        showLoadingSpinner(state)
                                        populateOutputDiv(
                                            api,
                                            outputDiv,
                                            TimeRange.values().first { it.id == this@select.value },
                                            state
                                        )
                                    }
                                }

                                selectedIndex = 0

                            }
                        }
                        outputDiv = div {}
                        populateOutputDiv(api, outputDiv, TimeRange.SHORT_TERM, state)

                        fixDropdownMobile()
                    }
                }
            }
        }
    }
})

@OptIn(ExperimentalTime::class)
fun populateOutputDiv(api: SpotifyImplicitGrantApi, outputDiv: Div, timeRange: TimeRange, state: SiteState) {
    outputDiv.removeAll()
    GlobalScope.launch {
        val limit = 50
        val topTracks = api.personalization.getTopTracks(limit, timeRange = timeRange).filterNotNull()
        val topArtists = api.personalization.getTopArtists(limit, timeRange = timeRange).filterNotNull()

        with(outputDiv) {
            val paginationSize = 10

            tabPanel {
                tab("Top Tracks", "fas icon-play-circle") {
                    tabulator(
                        topTracks.map { it.asTrackPreview() },
                        options = TabulatorOptions(
                            layout = Layout.FITCOLUMNS,
                            pagination = PaginationMode.LOCAL,
                            paginationSize = paginationSize,
                            paginationSizeSelector = true,
                            resizableColumns = false,
                            columns = listOf(
                                ColumnDefinition(
                                    "Track",
                                    "name",
                                    formatterComponentFunction = { cellComponent, f, trackPreview ->
                                        Div {
                                            cellComponent.getRow().normalizeHeight
                                            val track = topTracks.first { it.id == trackPreview.id }
                                            TrackPreviewComponent(
                                                trackPreview,
                                                this,
                                                bottomComponent = { span("Popularity: ${track.popularity}/100. Duration: ${track.durationMs.milliseconds}") },
                                                target = "_blank"
                                            )
                                        }
                                    }
                                )
                            ),
                            headerVisible = false,
                        )
                    )
                }

                tab("Top Artists", "fas icon-user") {
                    tabulator(
                        topArtists.map { it.asArtistPreview() },
                        options = TabulatorOptions(
                            layout = Layout.FITCOLUMNS,
                            pagination = PaginationMode.LOCAL,
                            paginationSize = paginationSize,
                            paginationSizeSelector = true,
                            resizableColumns = false,
                            columns = listOf(
                                ColumnDefinition(
                                    "Track",
                                    "name",
                                    formatterComponentFunction = { cellComponent, f, artistPreview ->
                                        Div {
                                            cellComponent.getRow().normalizeHeight
                                            val artist = topArtists.first { it.id == artistPreview.id }
                                            ArtistPreviewComponent(
                                                artistPreview,
                                                this,
                                                bottomComponent = { span("Popularity: ${artist.popularity}/100. Genres: ${artist.genres.joinToString(", ")}") },
                                                target = "_blank"
                                            )
                                        }
                                    }
                                )
                            ),
                            headerVisible = false,
                        )
                    )
                }
            }

            addIconsToFasElements()
            removeLoadingSpinner(state)
        }
    }
}

fun TimeRange.toReadable() = when (this) {
    TimeRange.SHORT_TERM -> "Short Term (past month)"
    TimeRange.MEDIUM_TERM -> "Medium Term (past 6 months)"
    TimeRange.LONG_TERM -> "Long Term (past several years, including recently played)"
}