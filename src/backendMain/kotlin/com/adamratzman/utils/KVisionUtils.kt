package com.adamratzman.utils

import io.ktor.application.ApplicationCall
import io.ktor.http.content.resolveResource
import io.ktor.response.respond

suspend fun ApplicationCall.renderSiteIndex() = respond(resolveResource("assets/index.html")!!)