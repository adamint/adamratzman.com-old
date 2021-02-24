package com.adamratzman.layouts.projects

import com.adamratzman.database.SiteManager.domain
import com.adamratzman.database.View.*
import com.adamratzman.layouts.NotFoundComponent
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.services.DailySongServiceFrontend
import com.adamratzman.services.SerializableDate
import com.adamratzman.services.UserRole
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.getSearchParams
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import com.adamratzman.utils.showDefaultErrorToast
import io.kvision.core.Container
import io.kvision.html.*
import io.kvision.remote.ServiceException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ViewDailySongComponent(date: SerializableDate, parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    div(classes = nameSetOf(MarginMediumTop, TextCenter)) {
        GlobalScope.launch {
            try {
                val dailySong = DailySongServiceFrontend.getDay(date)
                h3(classes = nameSetOf(MarginRemoveBottom, "moderate-bold")) {
                    +"Daily Song ${date.monthNumber + 1}/${date.dayOfMonth}/${date.year} ("
                    link(label = "Go back", url = ViewAllDailySongsPage.devOrProdUrl())
                    +")"
                }
                h2(classes = nameSetOf(MarginRemoveTop, MarginSmallBottom, MarginAuto, "moderate-bold", "max-width-1-2@m")) {
                    b(content = dailySong.trackName)
                }
                p(classes = nameSetOf(MarginRemoveTop, "light")) {
                    +"By "
                    dailySong.artists.forEachIndexed { i, artist ->
                        link(label = artist.name, url = SpotifyArtistViewPage(artist.spotifyId).devOrProdUrl())
                        if (i != dailySong.artists.lastIndex) +", "
                    }
                }
                if (dailySong.genres?.isNotEmpty() == true) {
                    p(content = "Genres: ${dailySong.genres.joinToString(", ")}", classes = nameSetOf(MarginRemoveTop, "light"))
                }

                iframe(
                    src = "https://open.spotify.com/embed/track/${dailySong.trackId}",
                    iframeWidth = 300,
                    iframeHeight = 300,
                    classes = nameSetOf(MarginAuto, "margin-medium-bottom@m", "margin-small-bottom@s")
                )

                if (dailySong.note != null) {
                    div(classes = nameSetOf(MarginAuto, "margin-medium-bottom@m", "margin-small-bottom@s", "max-width-1-2@m")) {
                        div(content = dailySong.note, rich = true)
                    }
                }

                val searchParams = getSearchParams()

                if (dailySong.protectedNote != null && searchParams.get("protected") == dailySong.protectedNotePassword) {
                    div(classes = nameSetOf(MarginAuto, "margin-medium-bottom@m", "margin-small-bottom@s", "max-width-1-2@m")) {
                        div(content = "Special note: ${dailySong.protectedNote}", rich = true)
                    }
                }

                if (state.clientSideData?.role == UserRole.Admin || searchParams.get("protected") == dailySong.protectedNotePassword) {
                    val protectedUrl = "${ViewDailySongPage(date).devOrProdUrl()}?protected=${dailySong.protectedNotePassword}"
                    p(className = MarginLargeTop.asString) {
                        +"The URL for this daily song, including the special note, is "
                        link(label = "$domain$protectedUrl", url = protectedUrl)
                    }
                }
            } catch (exception: ServiceException) {
                NotFoundComponent(this@div)
                exception.showDefaultErrorToast()
            }
        }

        removeLoadingSpinner(state)
    }
})