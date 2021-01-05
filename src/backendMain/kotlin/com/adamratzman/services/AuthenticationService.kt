package com.adamratzman.services

import com.adamratzman.database.*
import com.google.inject.Inject
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.auth.principal
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import pl.treksoft.kvision.remote.ServiceException

actual class AuthenticationService : IAuthenticationService {
    @Inject
    lateinit var call: ApplicationCall

    override suspend fun login(username: String, password: String): Boolean {
        if (call.principal<UserPrincipal>() != null) return true

        val incorrectCombination = "The username or password combination was incorrect."
        val user = getUser(username) ?: throw ServiceException(incorrectCombination)
        if (user.passwordSaltHash != getSaltedPassword(password, user.salt)) throw ServiceException(incorrectCombination)

        call.sessions.set(user)
        return true
    }


    override suspend fun register(username: String, password: String): Boolean {
        if (getUser(username) != null) throw ServiceException("That username is already taken.")
        if (!doesUsernameMeetRequirements(username)) throw ServiceException(usernameRequirementValidationErrorMessage)
        if (!doesPasswordMeetRequirements(password)) throw ServiceException(passwordRequirementValidationErrorMessage)

        val salt = generateSalt()
        val saltedPassword = getSaltedPassword(password, salt)


        val newUserPrincipal = UserPrincipal(
            username,
            salt,
            saltedPassword,
            UserRole.USER
        )

            call.sessions.set(newUserPrincipal)

            return transaction {
                Users.insert {
                    newUserPrincipal.addTo(it)
                }

                true
            }

    }

    override suspend fun getClientSideData(): ClientSideData {
        return call.sessions.get<UserPrincipal>()?.toClientSideData() ?: throw ServiceException("You're not logged in!")
    }
}