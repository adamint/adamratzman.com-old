package com.adamratzman.database

import com.adamratzman.services.ClientSideData
import com.adamratzman.services.UserRole
import io.ktor.auth.Principal
import io.ktor.util.getDigestFunction
import org.apache.commons.lang3.RandomStringUtils
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

data class UserPrincipal(
    val username: String,
    val salt: String,
    val passwordSaltHash: String,
    val role: UserRole
) : Principal {
    fun toClientSideData() = ClientSideData(username, role)
}

const val SessionAuthName = "session"

object Users : Table() {
    val username = varchar("username", 64)
    val passwordSaltHash = text("password")
    val salt = text("salt")
    val role = enumeration("role", UserRole::class)

    override val primaryKey = PrimaryKey(username)
}

fun ResultRow.toUserPrincipal() = UserPrincipal(
    username = this[Users.username],
    passwordSaltHash = this[Users.passwordSaltHash],
    salt = this[Users.salt],
    role = this[Users.role]
)

fun getUser(username: String): UserPrincipal? {
    return transaction {
        Users.select { Users.username.eq(username) }.firstOrNull()?.toUserPrincipal()
    }
}

fun getSaltedPassword(passwordPlaintext: String, salt: String) = getDigestFunction("SHA-256") { salt }.invoke(passwordPlaintext).joinToString(", ")

infix fun <T : Any> UserPrincipal.addTo(insertStatement: InsertStatement<T>) = insertStatement.apply {
    this[Users.username] = username
    this[Users.passwordSaltHash] = passwordSaltHash
    this[Users.salt] = salt
    this[Users.role] = role
}

fun generateSalt(): String = RandomStringUtils.randomAlphanumeric(16)