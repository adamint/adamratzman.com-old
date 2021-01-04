package com.adamratzman.routes

import com.adamratzman.database.SessionAuthName
import com.adamratzman.utils.renderSiteIndex
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.routing.Routing
import io.ktor.routing.get

fun Routing.profileRoutes() {
    profileHomeRoute()
}

fun Routing.profileHomeRoute() {
    authenticate(SessionAuthName) {
        get("/me") {
            call.renderSiteIndex()
        }
    }
}