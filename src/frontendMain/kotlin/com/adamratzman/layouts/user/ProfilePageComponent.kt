@file:UseContextualSerialization(Date::class)

package com.adamratzman.layouts.user

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.LogoutPage
import com.adamratzman.database.View.ViewAllDailySongsPage
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.security.guardLoggedIn
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.services.*
import com.adamratzman.spotify.models.SpotifyUri
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addBootstrap
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.core.UNIT.rem
import pl.treksoft.kvision.core.onEvent
import pl.treksoft.kvision.core.style
import pl.treksoft.kvision.form.formPanel
import pl.treksoft.kvision.form.text.richText
import pl.treksoft.kvision.form.text.text
import pl.treksoft.kvision.form.time.dateTime
import pl.treksoft.kvision.html.*
import pl.treksoft.kvision.remote.ServiceException
import kotlin.js.Date

class ProfilePageComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardLoggedIn(state) { clientSideData ->
        div(classes = nameSetOf(MarginAuto, WidthTwoThirds, MarginMediumTop)) {
            h2(classes = nameSetOf("light", MarginRemoveBottom)) {
                style { fontSize = 2.5 to rem }
                +"Hi, "
                span(content = clientSideData.username, className = "bold")
                +"."
            }
            p {
                +"You're a "
                span(clientSideData.role.readable, className = "dashed")
            }

            if (clientSideData.role == UserRole.ADMIN) {
                div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
                    InsertDailySongComponent(this)
                }
            }
        }

        removeLoadingSpinner(state)
    }
})

private class InsertDailySongComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        h2("Add a daily song (or replace an existing day)", classes = nameSetOf("light"))

        var protectedNotePassword = (1..16).joinToString("") { (('a'..'z') + ('A'..'Z') + ('0'..'9')).random().toString() }

        addBootstrap()
        var autofilledDate: Date? = null
        formPanel<InsertDailySongForm> {
            add(
                InsertDailySongForm::date,
                dateTime(format = "YYYY-MM-DD", label = "Date").apply {
                    placeholder = "Enter date"
                    showTodayButton = true
                }.apply {
                    onEvent {
                        change = {
                            val data = getData()
                            if (data.date != null &&
                                (data.date.getFullYear() != autofilledDate?.getFullYear() || data.date.getMonth() != autofilledDate?.getMonth()
                                        || data.date.getDate() != autofilledDate?.getDate())) {
                                data.date.let { date ->
                                    GlobalScope.launch {
                                        try {
                                            val dailySong = DailySongServiceFrontend.getDay(
                                                SerializableDate(
                                                    date.getFullYear(),
                                                    date.getMonth(),
                                                    date.getDate()
                                                )
                                            )
                                            autofilledDate = date
                                            if (data.trackUri?.let { SpotifyUri(it).id } != dailySong.trackId) {
                                                setData(
                                                    data.copy(
                                                        trackUri = "spotify:track:${dailySong.trackId}",
                                                        note = dailySong.note,
                                                        protectedNote = dailySong.protectedNote
                                                    )
                                                )
                                                protectedNotePassword = dailySong.protectedNotePassword
                                            }
                                        } catch (ignored: ServiceException) {
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                required = true
            )

            add(
                InsertDailySongForm::trackUri,
                text(label = "Spotify Track URI") {
                    placeholder = "Enter spotify track uri"
                },
                required = true
            )

            add(
                InsertDailySongForm::note,
                richText(label = "Note") {
                    placeholder = "Enter an optional note"
                }
            )

            add(
                InsertDailySongForm::protectedNote,
                richText(label = "Protected note") {
                    placeholder = "Enter an optional protected note that would be accessed with the key $protectedNotePassword"
                }
            )

            button("Add/Replace") {
                onClick {
                    GlobalScope.launch {
                        if (validate()) {
                            val formData = getData()
                            val date = formData.date!!

                            val track = api.tracks.getTrack(SpotifyUri(formData.trackUri!!).id)!!

                            val dailySong = DailySong(
                                SerializableDate(
                                    date.getFullYear(),
                                    date.getMonth(),
                                    date.getDate()
                                ),
                                track.id,
                                formData.note,
                                formData.protectedNote,
                                protectedNotePassword,
                                track.artists.map { artist -> ArtistNameAndSpotifyId(artist.name, artist.id) },
                                track.name,
                                api.albums.getAlbum(track.album.id)?.genres,
                                track.album.images.firstOrNull()?.url
                            )

                            if (!DailySongServiceFrontend.addOrUpdate(dailySong)) SiteManager.redirectToUrl(LogoutPage.devOrProdUrl())
                            else {
                                SiteManager.redirectToUrl(ViewAllDailySongsPage.devOrProdUrl())
                            }
                        }
                    }
                }
            }
        }
    }
})

@Serializable
data class InsertDailySongForm(
    val date: Date? = null,
    val trackUri: String? = null,
    val note: String? = null,
    val protectedNote: String? = null
)