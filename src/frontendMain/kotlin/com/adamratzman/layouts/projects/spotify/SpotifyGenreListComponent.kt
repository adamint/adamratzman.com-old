package com.adamratzman.layouts.projects.spotify

import com.adamratzman.layouts.SiteStatefulComponent
import io.ktor.client.HttpClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.treksoft.kvision.core.Container
import pl.treksoft.kvision.html.p

lateinit var genres: List<String>

class SpotifyGenreListComponent(parent: Container) : SiteStatefulComponent(parent = parent, buildStatefulComponent = { state ->
    guardValidSpotifyApi(state) { api ->
        GlobalScope.launch {
            //   println(client.get<String>("https://github.com"))
            if (!::genres.isInitialized) genres = api.browse.getAvailableGenreSeeds()
            println(genres.toList())
            println(genres.first())
            p(content = "display name: " + Json.encodeToString(api.users.getClientProfile()))

        }

    }
})