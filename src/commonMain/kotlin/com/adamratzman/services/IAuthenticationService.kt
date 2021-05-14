package com.adamratzman.services

import kotlinx.serialization.Serializable
import io.kvision.annotations.KVService

const val passwordRequirementValidationErrorMessage = "The password must be at least 8 characters and contain a number."
fun doesPasswordMeetRequirements(password: String) = password.length >= 8 && password.any { it in '0'..'9' }

const val usernameRequirementValidationErrorMessage = "The username must be at least 3 characters."
fun doesUsernameMeetRequirements(username: String) = username.length >= 3

@KVService
interface IAuthenticationService {
    suspend fun login(username: String, password: String): Boolean
    suspend fun register(username: String, password: String): Boolean
    suspend fun getClientSideData(): ClientSideData
}

@Serializable
data class ClientSideData(
    val username: String,
    val role: UserRole
)

enum class UserRole(val readable: String) {
    User("user"),
    Admin("site admin")
}