package com.adamratzman

import com.adamratzman.database.*
import com.adamratzman.routes.authRoutes
import com.adamratzman.routes.profileRoutes
import com.adamratzman.services.*
import com.adamratzman.utils.renderSiteIndex
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.AuthenticationFailedCause.InvalidCredentials
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.path
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import io.ktor.util.KtorExperimentalAPI
import io.kvision.remote.ServiceException
import io.kvision.remote.applyRoutes
import io.kvision.remote.kvisionInit
import java.io.File
import java.security.KeyStore

val isProd: Boolean = System.getenv("IS_PROD").toBoolean()

fun Application.module() {
    SiteDatabase.initialize()

    install(Compression)
    install(Sessions) {
        cookie<UserPrincipal>(UserPrincipalCookieName) {
            cookie.path = "/"
            cookie.extensions["SameSite"] = "lax"
        }

    }
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
            println("Not found: ${call.request.path()}")
            call.renderSiteIndex()
        }
    }
    install(Authentication) {
        session<UserPrincipal>(SessionAuth) {
            challenge {
                call.respondRedirect("/login")
            }
            validate { session: UserPrincipal ->
                session
            }
        }

        form(FormAuth) {
            userParamName = "username"
            passwordParamName = "password"
            challenge {
                val errors: List<AuthenticationFailedCause> = call.authentication.allFailures
                when (errors.singleOrNull()) {
                    InvalidCredentials -> call.respondRedirect("/login?error=invalid_credentials")
                    else -> call.respondRedirect("/login")
                }
            }
            validate { cred: UserPasswordCredential ->
                val user = getUser(cred.name) ?: return@validate null
                return@validate (if (user.passwordSaltHash == getSaltedPassword(cred.password, user.salt)) {
                    UserPrincipal.generate(user)
                }
                else null)
            }
        }
    }


    routing {
        authRoutes()
        static("static") {
            resources("assets/static")
        }

        get("/login") {
            if (call.principal<UserPrincipal>() != null) call.respondRedirect("/me")
            else call.renderSiteIndex()
        }

        get("/register") {
            if (call.principal<UserPrincipal>() != null) call.respondRedirect("/me")
            else call.renderSiteIndex()
        }

        get("/logout") {
            call.sessions.clear<UserPrincipal>()
            call.authentication.call.respondRedirect("/login?logout=true")
        }
    }

    kvisionInit()

    routing {
        applyRoutes(BaseConversionServiceManager)
        applyRoutes(UrlShortenerServiceManager)
        applyRoutes(CalculatorServiceManager)
        applyRoutes(AuthenticationServiceManager)
        applyRoutes(DailySongServiceManager)
        applyRoutes(BlogServiceManager)
        profileRoutes()
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
