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
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resolveResource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import pl.treksoft.kvision.remote.ServiceException
import pl.treksoft.kvision.remote.applyRoutes
import pl.treksoft.kvision.remote.kvisionInit
import java.io.File
import java.security.KeyStore

val isProd = System.getenv("IS_PROD").toBoolean()

fun Application.module() {
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
                call.respondText(
                    throwable.message
                        ?: throwable.localizedMessage, ContentType.Any, HttpStatusCode.InternalServerError
                )
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

@KtorExperimentalAPI
fun main() {
    if (isProd) {
        embeddedServer(Netty, applicationEngineEnvironment {
            modules.add(Application::module)

            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            val keyStoreFile = File("/ssl/keystore-self-signed.jks")
            val keystorePassword = System.getenv("KEYSTORE_PASSWORD")
            keyStore.load(keyStoreFile.inputStream(), keystorePassword.toCharArray())

            sslConnector(
                keyStore, "site", { keystorePassword.toCharArray() }, { keystorePassword.toCharArray() }
            ) {
                host = "0.0.0.0"
                port = 443
            }
        }).start(wait = true)
    } else {
        embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
    }
}
