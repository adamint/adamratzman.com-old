package com.adamratzman.layouts.projects.spotify

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.SpotifyArtistViewPage
import com.adamratzman.database.View.SpotifyGenreListPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.layouts.projects.goBackToProjectHome
import com.adamratzman.layouts.setTitle
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.endpoints.public.TuneableTrackAttribute
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.RecommendationResponse
import com.adamratzman.spotify.models.Track
import com.adamratzman.utils.*
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.UikitName.Icon
import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pl.treksoft.kvision.core.*
import pl.treksoft.kvision.core.UNIT.*
import pl.treksoft.kvision.form.range.rangeInput
import pl.treksoft.kvision.form.select.Select
import pl.treksoft.kvision.form.select.select
import pl.treksoft.kvision.form.text.typeahead
import pl.treksoft.kvision.html.*

private lateinit var allSpotifyGenres: List<String>

@Suppress("UselessCallOnCollection")
class SpotifyPlaylistGeneratorComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        setTitle(state.view.name)
        var currentAttributesDiv: Div? = null
        var selectDesiredAttributesSelect: Select? = null
        var recommendationOutputDiv: Div? = null
        val requestInfo = SpotifyPlaylistGeneratorRequest()

        fun getFormInputs() = AssociatedFormInputs(
            currentAttributesDiv!!,
            selectDesiredAttributesSelect,
            recommendationOutputDiv,
            requestInfo,
            api
        )

        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
            addUikitAttributes(UkGridAttribute)
            // div(className = "margin-left-10@m")
            div(classes = nameSetOf("margin-left-10@m", WidthOneHalf.medium)) {
                h2(
                    content = "Generate your own Spotify playlist",
                    classes = nameSetOf(MarginRemoveBottom, "moderate-bold")
                )
                goBackToProjectHome()

                p(classes = nameSetOf(MarginSmallTop, MarginSmallBottom)) {
                    +"To filter songs by artist, add \"by first artist, second artist, etc.\""
                }
                div(classes = nameSetOf(MarginMediumBottom.asString)) {
                    addBootstrap()
                    h3(classes = nameSetOf(UkH3, MarginSmallBottom, MarginRemoveTop)) {
                        +"Songs, artists, and "
                        link(label = "genres", url = SpotifyGenreListPage.devOrProdUrl(), classes = nameSetOf("link-color"))
                        +" (at least one is required)"
                        +" "
                        span(classes = nameSetOf(UkInline.asString)) {
                            addAttributes(
                                UkTooltipAttribute to "You can add up to 5 of any combination of Spotify track, artist, or genre URIs.",
                                IconAttribute to "icon: info;"
                            )
                        }
                    }
                    typeahead(source = { input, updateFunction ->
                        GlobalScope.launch {
                            if (!::allSpotifyGenres.isInitialized) {
                                allSpotifyGenres = api.browse.getAvailableGenreSeeds()
                            }

                            val matches = getMatches(input, api)
                            updateFunction(matches.flatten().toTypedArray())
                        }
                    }) {
                        placeholder = "Enter a song, artist, or genre uri.."

                        subscribe { input ->
                            if (input?.matches("^.+(\\(artist\\)|\\(genre\\)|\\(song\\))") == true) {
                                GlobalScope.launch {
                                    when {
                                        input.endsWith(" (artist)") -> {
                                            val artistName = input.removeSuffix(" (artist)")
                                            api.search.searchArtist(artistName).first()?.let { artist ->
                                                requestInfo.artistsToSearch.add(artist)
                                            }
                                        }
                                        input.endsWith(" (genre)") -> {
                                            allSpotifyGenres.find { it == input.removeSuffix(" (genre)") }?.let { genre ->
                                                requestInfo.genresToSearch.add(genre)
                                            }
                                        }
                                        input.endsWith(" (song)") -> {
                                            val unparsedSongWithArtists = input.removeSuffix(" (song)")
                                            val foundSongNameAndArtists = parseInputToFoundSongNameAndArtists(unparsedSongWithArtists)
                                            api.search.searchTrack(foundSongNameAndArtists.songName, limit = 25)
                                                .filterNotNull()
                                                .first { track ->
                                                    val trackArtists = track.artists.map {
                                                        it.name.toLowerCase().removeAllMatchedSubstrings(" ")
                                                    }
                                                    track.name == foundSongNameAndArtists.songName
                                                            && foundSongNameAndArtists.artistsNames.map {
                                                        it.toLowerCase().removeAllMatchedSubstrings(" ")
                                                    }.all { filterArtist -> trackArtists.any { filterArtist in it } }
                                                }.let { foundTrack -> requestInfo.tracksToSearch.add(foundTrack) }
                                        }
                                    }

                                    this@typeahead.input.value = null
                                    currentAttributesDiv?.let { RequestAttributesComponent(getFormInputs(), requestInfo) }
                                }
                            }
                        }
                    }

                    selectDesiredAttributesSelect = select(
                        label = "Desired Attributes",
                        options = TuneableTrackAttribute.values().map { it.attribute to it.name() }
                    ) {
                        addCssClass(MarginLargeBottom.asString)

                        this.placeholder = "Select a track attribute.."
                        this.multiple = true

                        subscribe { input ->
                            this.unfocus()

                            currentAttributesDiv?.p()
                            if (input == null) {
                                currentAttributesDiv?.let { RequestAttributesComponent(getFormInputs(), requestInfo) }
                                return@subscribe
                            }
                            val trackAttributes = input.split(",").mapNotNull { attribute ->
                                TuneableTrackAttribute.values().find { it.attribute == attribute }
                            }

                            trackAttributes.forEach { trackAttribute ->
                                if (trackAttribute.attribute !in requestInfo.attributes) {
                                    requestInfo.attributes[trackAttribute.attribute] = trackAttribute.defaultValue()
                                }
                            }
                            requestInfo.attributes.keys.forEach { attributeKey ->
                                if (attributeKey !in trackAttributes.map { it.attribute }) requestInfo.attributes.remove(
                                    attributeKey
                                )
                            }

                            currentAttributesDiv?.let { RequestAttributesComponent(getFormInputs(), requestInfo) }
                        }
                    }
                }

            }
            currentAttributesDiv =
                div(classes = nameSetOf(WidthOneFourth.medium, FloatRight, "margin-right-10@m"))
            RequestAttributesComponent(getFormInputs(), requestInfo)
        }

        recommendationOutputDiv = div(classes = nameSetOf("margin-left-10@m", WidthOneHalf.medium))
    }
})

private suspend fun getMatches(input: String, api: SpotifyImplicitGrantApi): List<List<String>> {
    return coroutineScope {
        listOf(
            async {
                allSpotifyGenres.filter { genre ->
                    input.toLowerCase().removeAllMatchedSubstrings(" ", "-") in
                            genre.removeAllMatchedSubstrings(" ", "-")
                }.map { "$it (genre)" }
            },
            async {
                val songNameAndArtists = parseInputToFoundSongNameAndArtists(input)
                val songName = songNameAndArtists.songName
                val filterArtists = songNameAndArtists.artistsNames

                var matchedTracks =
                    api.search.searchTrack(songName, limit = 20).filterNotNull()
                if (filterArtists.isNotEmpty()) matchedTracks =
                    matchedTracks.filter { track ->
                        val trackArtists = track.artists.map {
                            it.name.toLowerCase().removeAllMatchedSubstrings(" ")
                        }
                        filterArtists.map {
                            it.toLowerCase().removeAllMatchedSubstrings(" ")
                        }.all { filterArtist ->
                            trackArtists.any { filterArtist in it }
                        }
                    }

                matchedTracks.map { track ->
                    "${track.name} by ${track.artists.joinToString(", ") { it.name }} (song)"
                }
            },
            async {
                api.search.searchArtist(input, limit = 10).filterNotNull()
                    .map { artist ->
                        "${artist.name} (artist)"
                    }
            }
        ).awaitAll()
    }
}

private fun parseInputToFoundSongNameAndArtists(input: String): FoundSongNameAndArtist {
    val songNameWithArtistRegex = "^(.+) by (.+)".toRegex()
    val songNameArtistPair = songNameWithArtistRegex.matchEntire(input)
        ?.let {
            val (name, artistsJoinedByComma) = it.destructured
            name to artistsJoinedByComma.split(", ").toList()
        } ?: input to listOf()
    return FoundSongNameAndArtist(songNameArtistPair.first, songNameArtistPair.second)
}

private data class SpotifyPlaylistGeneratorRequest(
    val attributes: MutableMap<String, Float> = mutableMapOf(),
    val tracksToSearch: MutableSet<Track> = mutableSetOf(),
    val genresToSearch: MutableSet<String> = mutableSetOf(),
    val artistsToSearch: MutableSet<Artist> = mutableSetOf(),
) {
    fun validateNotEmpty() = artistsToSearch.isNotEmpty() || tracksToSearch.isNotEmpty() || genresToSearch.isNotEmpty()
    fun validateSizeNotGreaterThan5() = (artistsToSearch + tracksToSearch + genresToSearch).size <= 5
}

private data class FoundSongNameAndArtist(
    val songName: String,
    val artistsNames: List<String>
)

private fun TuneableTrackAttribute<*>.min() = if (this == TuneableTrackAttribute.Loudness) -70 else min!!

private fun TuneableTrackAttribute<*>.max() = when (this) {
    TuneableTrackAttribute.DurationInMilliseconds -> 60
    TuneableTrackAttribute.Loudness -> 10
    TuneableTrackAttribute.Tempo -> 300
    else -> max!!
}

private fun TuneableTrackAttribute<*>.step() =
    if (!integerOnly || this == TuneableTrackAttribute.DurationInMilliseconds) 0.01 else null

private fun TuneableTrackAttribute<*>.defaultValue() = when (this) {
    is TuneableTrackAttribute.DurationInMilliseconds -> 3
    is TuneableTrackAttribute.Tempo -> 120
    is TuneableTrackAttribute.Loudness -> -8
    else -> (max().toFloat() - min().toFloat()) / 2
}.toFloat()

private fun TuneableTrackAttribute<*>.name() = when (this) {
    TuneableTrackAttribute.DurationInMilliseconds -> "Duration (minutes)"
    else -> attribute.replace("_", " ").capitalize()
}


private data class AssociatedFormInputs(
    val currentAttributesDiv: Div,
    val selectDesiredAttributesSelect: Select?,
    val recommendationOutputDiv: Div?,
    val requestInfo: SpotifyPlaylistGeneratorRequest,
    val api: SpotifyImplicitGrantApi
)

private class RequestAttributesComponent(formInputs: AssociatedFormInputs, requestInfo: SpotifyPlaylistGeneratorRequest) :
    SiteStatefulComponent(parent = formInputs.currentAttributesDiv, buildStatefulComponent = {
        with(formInputs.currentAttributesDiv) {
            val attributeDivContent = Div(classes = nameSetOf("dotted-box")) {
                if (!requestInfo.validateNotEmpty() && requestInfo.attributes.isEmpty()) {
                    p("There are no set request attributes. Please add some.")
                } else {
                    SearchFilterTypeComponent(
                        "Genres",
                        requestInfo.genresToSearch,
                        String::toString,
                        formInputs,
                        requestInfo,
                        this
                    )
                    SearchFilterTypeComponent(
                        "Tracks",
                        requestInfo.tracksToSearch,
                        { track -> "${track.name} by ${track.artists.joinToString(", ") { it.name }}" },
                        formInputs,
                        requestInfo,
                        this
                    )
                    SearchFilterTypeComponent(
                        "Artists", requestInfo.artistsToSearch, { artist -> "${artist.name} (${artist.popularity})" },
                        formInputs, requestInfo, this
                    )

                    SearchTuneableTrackAttributesComponent(formInputs, requestInfo, this)


                }

                if (!requestInfo.validateNotEmpty()) p {
                    style { color = Color.name(Col.RED) }
                    +"You need to specify at least one artist, genre, or song as the playlist seed."
                } else if (!requestInfo.validateSizeNotGreaterThan5()) p {
                    style { color = Color.name(Col.RED) }
                    +"You can only specify 5 of any combination of artists, genres, and songs."
                }
            }
            removeAll()
            add(attributeDivContent)
        }

        with(formInputs.recommendationOutputDiv) {
            if (this == null || !requestInfo.validateNotEmpty() || !requestInfo.validateSizeNotGreaterThan5()) {
                this?.removeAll()
                return@with
            }
            GlobalScope.launch {
                try {
                    val recommendationResponse = formInputs.api.browse.getTrackRecommendations(
                        requestInfo.artistsToSearch.map { it.id },
                        requestInfo.genresToSearch.toList(),
                        requestInfo.tracksToSearch.map { it.id },
                        targetAttributes = requestInfo.attributes.map { entry ->
                            val trackAttribute = TuneableTrackAttribute.values().first { it.attribute == entry.key }
                            when {
                                trackAttribute.integerOnly -> trackAttribute.asTrackAttribute(entry.value.toInt())
                                else -> trackAttribute.asTrackAttribute(entry.value)
                            }
                        },
                        limit = 20
                    )

                    RecommendedPlaylistComponent(formInputs, recommendationResponse, this@with)
                } catch (exception: IllegalStateException) {
                    SiteManager.redirectToSpotifyAuthentication(this@with)
                }
            }
        }
    })

private class SearchTuneableTrackAttributesComponent(
    formInputs: AssociatedFormInputs,
    requestInfo: SpotifyPlaylistGeneratorRequest,
    parent: Container
) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        if (requestInfo.attributes.isNotEmpty()) {
            h5("Song Attributes")
            ul {
                requestInfo.attributes.keys.forEach { attribute ->
                    val trackAttribute = TuneableTrackAttribute.values().first { it.attribute == attribute }
                    li {
                        +"${trackAttribute.name()}: "
                        val attributeValueBold = bold()

                        fun setAttributeValue() {
                            attributeValueBold.apply {
                                removeAll()
                                val attributeValue = requestInfo.attributes.getValue(attribute)
                                if (trackAttribute.integerOnly) +"${attributeValue.toInt()}"
                                else +"$attributeValue"
                            }
                        }
                        setAttributeValue()

                        link(label = " ", classes = nameSetOf(Icon)) {
                            addAttributes(IconAttribute to "close")
                            onClick {
                                requestInfo.attributes.remove(attribute)
                                RequestAttributesComponent(formInputs, requestInfo)

                                formInputs.selectDesiredAttributesSelect?.let { select ->
                                    select.value =
                                        select.value?.replace(
                                            "(($attribute,)(.+))?((.+)(,$attribute))?(^$attribute)?".toRegex(),
                                            "\$3\$5"
                                        )
                                }
                            }
                        }
                        rangeInput(
                            requestInfo.attributes.getValue(attribute),
                            trackAttribute.min(),
                            trackAttribute.max(),
                            trackAttribute.step() ?: 1,
                            classes = nameSetOf(MarginSmallTop, MarginSmallBottom, UkRange)
                        ) {
                            onEvent {
                                change = {
                                    this@rangeInput.value?.let { value ->
                                        requestInfo.attributes[attribute] = value.toFloat()
                                        setAttributeValue()
                                        RequestAttributesComponent(formInputs, requestInfo)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    })

private class SearchFilterTypeComponent<T>(
    typeName: String,
    list: MutableSet<T>,
    stringifyT: (T) -> String,
    formInputs: AssociatedFormInputs,
    requestInfo: SpotifyPlaylistGeneratorRequest,
    parent: Container
) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        if (list.isNotEmpty()) {
            div {
                h5(typeName)
                ul {
                    list.forEach { value ->
                        li {
                            +stringifyT(value)
                            link(label = " ", classes = nameSetOf(Icon)) {
                                addAttributes(IconAttribute to "close")
                                onClick {
                                    list.remove(value)
                                    RequestAttributesComponent(formInputs, requestInfo)
                                }
                            }
                        }
                    }
                }
            }
        }
    })

private class RecommendedPlaylistComponent(formInputs: AssociatedFormInputs, recommendationResponse: RecommendationResponse, parent: Container) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        parent.removeAll()
        h3("Recommended Tracks (${recommendationResponse.tracks.size})")
        p {
            link(label = "Create your playlist →", classes = nameSetOf("link-color", "link-color")) {
                onClick {
                    GlobalScope.launch {
                        val newPlaylist = formInputs.api.playlists.createClientPlaylist(
                            "Your generated playlist (${Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())})",
                            "Created using the `Generate Your Own Spotify Playlist` web application at ${window.location.href}"
                        )
                        formInputs.api.playlists.setClientPlaylistTracks(
                            newPlaylist.id,
                            *(formInputs.requestInfo.tracksToSearch.map { it.id } + recommendationResponse.tracks.map { it.id }).toTypedArray()
                        )
                        window.open(url = newPlaylist.externalUrls.first { it.name == "spotify" }.url, target = "_blank")
                    }
                }
            }
        }

        div {
            recommendationResponse.tracks.forEach { track ->
                TrackPreviewComponent(
                    track,
                    this,
                    target = "_blank",
                    bottomComponent = {
                        span {
                            track.artists.forEachIndexed { i, artist ->
                                link(label = artist.name, url = SpotifyArtistViewPage(artist.id).devOrProdUrl(), classes = nameSetOf("black"))
                                if (i != track.artists.lastIndex) +", "
                            }
                        }
                    })
            }
        }

    })

