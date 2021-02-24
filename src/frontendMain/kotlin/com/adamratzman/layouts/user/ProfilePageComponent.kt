@file:UseContextualSerialization(Date::class)

package com.adamratzman.layouts.user

import com.adamratzman.database.SiteManager
import com.adamratzman.database.View.*
import com.adamratzman.layouts.SiteStatefulComponent
import com.adamratzman.security.guardLoggedIn
import com.adamratzman.security.guardValidSpotifyApi
import com.adamratzman.services.*
import com.adamratzman.spotify.models.SpotifyUri
import com.adamratzman.spotify.utils.getCurrentTimeMs
import com.adamratzman.utils.UikitName.*
import com.adamratzman.utils.addBootstrap
import com.adamratzman.utils.nameSetOf
import com.adamratzman.utils.removeLoadingSpinner
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import org.w3c.dom.get
import io.kvision.core.Container
import io.kvision.core.UNIT.px
import io.kvision.core.UNIT.rem
import io.kvision.core.onEvent
import io.kvision.core.style
import io.kvision.form.check.checkBox
import io.kvision.form.formPanel
import io.kvision.form.text.richText
import io.kvision.form.text.text
import io.kvision.form.time.dateTime
import io.kvision.html.*
import io.kvision.panel.tab
import io.kvision.panel.tabPanel
import io.kvision.remote.ServiceException
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

            if (clientSideData.role == UserRole.Admin) {
                tabPanel {
                    tab(label = "Blog") {
                        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
                            InsertBlogPostComponent(clientSideData, this)
                        }
                    }

                    tab(label = "Daily Songs") {
                        div(classes = nameSetOf(MarginMediumTop, MarginMediumBottom)) {
                            InsertDailySongComponent(this)
                        }
                    }
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
            onEvent {
                submit = {
                    it.preventDefault()
                    it.stopPropagation()
                }
            }
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
                                        || data.date.getDate() != autofilledDate?.getDate())
                            ) {
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

private class InsertBlogPostComponent(clientSideData: ClientSideData, parent: Container) :
    SiteStatefulComponent(parent = parent, buildStatefulComponent = {
        h2("Add a blog post (or edit or delete an existing one)", classes = nameSetOf("light"))
        h4("Posts:")
        GlobalScope.launch {
            val blogPosts = BlogServiceFrontend.blogService.getBlogPosts(listOf())
            ul {
                blogPosts.forEach { post ->
                    li {
                        link("${post.title} (${post.id})")
                    }
                }
            }
            addBootstrap()
            var autofilledBlogPost: BlogPost? = null
            formPanel<InsertBlogPostForm> {
                onEvent {
                    submit = {
                        it.preventDefault()
                        it.stopPropagation()
                    }
                }

                add(
                    InsertBlogPostForm::id,
                    text(label = "Post ID") {
                        placeholder = "Enter post id"

                        onEvent {
                            change = {
                                val enteredId = this@text.value
                                if (enteredId != null && autofilledBlogPost?.id != enteredId) {
                                    GlobalScope.launch {
                                        try {
                                            val blogPost = BlogServiceFrontend.blogService.getBlogPost(enteredId)
                                            autofilledBlogPost = blogPost
                                            setData(
                                                InsertBlogPostForm(
                                                    blogPost.id,
                                                    blogPost.title,
                                                    blogPost.categories.joinToString(","),
                                                    blogPost.richText,
                                                    blogPost.creationMillis,
                                                    blogPost.lastEditMillis,
                                                    blogPost.deleted
                                                )
                                            )
                                        } catch (ignored: ServiceException) {
                                        }
                                    }
                                }
                            }
                        }
                    },
                    required = true
                )

                add(
                    InsertBlogPostForm::title,
                    text(label = "Title"),
                    required = true
                )

                add(
                    InsertBlogPostForm::categoriesText,
                    text(label = "Categories (comma-separated)") {
                        placeholder = "Enter comma-separated categories for this post."
                    }
                )

                val textInput = richText(label = "Post content") {
                    removeCssClass("form-control")
                    input.removeCssClass("form-control")

                }
                add(
                    InsertBlogPostForm::richText,
                    textInput,
                    required = true
                )

                val imgInput = text(label = "IMG url to enter")
                button("Add img") {
                    marginBottom = 10 to px
                    onClick {
                        val editor = document.getElementsByTagName("trix-editor")[0].asDynamic().editor
                        if (editor != undefined) {
                            editor.insertHTML("<img src='${imgInput.value}' />")
                            imgInput.value = null
                        }
                    }
                }


                add(
                    InsertBlogPostForm::deleted,
                    checkBox(label = "Mark as deleted?")
                )

                val preview = div()
                button("Toggle preview") {
                    onClick {
                        if (preview.getChildren().isNotEmpty()) preview.removeAll()
                        else preview.div(rich = true, content = textInput.value)
                    }
                }


                button("Submit") {
                    onClick {
                        GlobalScope.launch onClickSubmit@ {
                            val data = getData()
                            if (data.categoriesText?.isNotBlank() != true) return@onClickSubmit

                            val prevPost = try {
                                BlogServiceFrontend.blogService.getBlogPost(data.id!!)
                            } catch (ignored: Exception) {
                                null
                            }

                            val blogPost = BlogPost(
                                data.id!!,
                                data.title!!,
                                clientSideData.username,
                                data.categoriesText.split(",").filter { it.isNotBlank() }.toMutableList(),
                                textInput.value!!,
                                prevPost?.creationMillis ?: getCurrentTimeMs(),
                                if (prevPost != null) getCurrentTimeMs() else null,
                                data.deleted
                            )

                            BlogServiceFrontend.blogService.createOrUpdateBlogPost(blogPost)

                            SiteManager.redirect(ViewBlogPostPage(blogPost.id))
                        }
                    }
                }
            }
        }
    })

@Serializable
data class InsertDailySongForm(
    @Contextual val date: Date? = null,
    val trackUri: String? = null,
    val note: String? = null,
    val protectedNote: String? = null
)

@Serializable
data class InsertBlogPostForm(
    val id: String? = null,
    val title: String? = null,
    val categoriesText: String? = null,
    var richText: String? = null,
    val creationMillis: Long? = null,
    var lastEditMillis: Long? = null,
    var deleted: Boolean = false
)
