package com.adamratzman.security

import pl.treksoft.kvision.remote.Credentials
import pl.treksoft.kvision.remote.LoginService
import pl.treksoft.kvision.remote.SecurityMgr

object SecurityManager : SecurityMgr() {
    private val loginService = LoginService("/login")
    var credentials: Credentials? = null

    override suspend fun login(): Boolean {
        return loginService.login(credentials)
    }

    override suspend fun afterLogin() {
// TODO
    }
}
