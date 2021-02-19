package com.adamratzman.routes

import com.adamratzman.database.FormAuth
import com.adamratzman.database.SessionAuth
import com.adamratzman.database.User
import com.adamratzman.database.UserPrincipal
import com.adamratzman.utils.renderSiteIndex
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.response.respondRedirect
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.sessions.sessions
import io.ktor.sessions.set

fun Routing.authRoutes() {
    route("/login") {
        authenticate(FormAuth) {
            post {
                val principal = call.principal<UserPrincipal>()!!
                call.sessions.set(principal)
                call.respondRedirect("/loggedIn")
            }
        }
    }

    authenticate(SessionAuth) {
        get("/loggedIn") {
            call.renderSiteIndex()
        }
    }
}