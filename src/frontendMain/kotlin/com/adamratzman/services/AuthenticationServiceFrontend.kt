package com.adamratzman.services

import com.adamratzman.utils.fixServiceRoutingWithOptionalBeforeSending

object AuthenticationServiceFrontend  {
    private val authenticationService = AuthenticationService(beforeSend = fixServiceRoutingWithOptionalBeforeSending())

    suspend fun login(username: String, password: String) = authenticationService.login(username, password)
    suspend fun register(username: String, password: String) = authenticationService.register(username, password)
    suspend fun getClientSideData() = authenticationService.getClientSideData()
}