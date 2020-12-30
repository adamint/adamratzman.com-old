package com.adamratzman

import com.adamratzman.database.SiteDatabase
import com.adamratzman.services.BaseConversionServiceManager
import com.adamratzman.services.CalculatorServiceManager
import com.adamratzman.services.UrlShortenerServiceManager
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpMethod.Companion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resolveResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import pl.treksoft.kvision.remote.ServiceException
import pl.treksoft.kvision.remote.applyRoutes
import pl.treksoft.kvision.remote.kvisionInit

fun Application.main() {
    SiteDatabase.initialize()

    install(Compression)
    install(CORS) {
        allowCredentials = true
        allowNonSimpleContentTypes = true
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        anyHost()
    }
    install(StatusPages) {
        exception<Throwable> { throwable ->
            if (throwable !is ServiceException) {
                throwable.printStackTrace()
                call.respondText(throwable.message
                        ?: throwable.localizedMessage, ContentType.Any, HttpStatusCode.InternalServerError)
            }
        }

        status(HttpStatusCode.NotFound) {
            println(call.request.path())
            call.respond(this.context.resolveResource("assets/index.html")!!)
            // call.respondFile(File("/assets/index.html"))

        }
    }
    routing {
        static("static") {
            resources("assets/static")
        }
    }

    kvisionInit()

    routing {
        applyRoutes(BaseConversionServiceManager)
        applyRoutes(UrlShortenerServiceManager)
        applyRoutes(CalculatorServiceManager)
    }
}
