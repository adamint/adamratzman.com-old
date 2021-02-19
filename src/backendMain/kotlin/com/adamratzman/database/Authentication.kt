package com.adamratzman.database

import com.adamratzman.services.ClientSideData
import com.adamratzman.services.UserRole
import io.ktor.auth.Principal
import io.ktor.util.getDigestFunction
import kotlinx.serialization.Serializable
import org.apache.commons.lang3.RandomStringUtils
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction

const val FormAuth = "session"
const val SessionAuth = "UserPrincipalSessionAuth"
const val UserPrincipalCookieName = "UserPrincipal"

object Users : IdTable<String>() {
    override val id: Column<EntityID<String>> = varchar("username", 64).entityId() // username
    override val primaryKey: PrimaryKey = PrimaryKey(id)

    val passwordSaltHash = text("password")
    val salt = text("salt")
    val role = enumeration("role", UserRole::class)
}

class UserPrincipal(val username: String): Principal {
    fun getUser(): User {
        return transaction {
            User[username]
        }
    }

    companion object {
        fun generate(user: User): UserPrincipal = UserPrincipal(user.username.value)
    }
}

class User(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, User>(Users)

    var username by Users.id
    var passwordSaltHash by Users.passwordSaltHash
    var salt by Users.salt
    var role by Users.role

    fun toClientSideData() = ClientSideData(username.value, role)
}

fun getUser(username: String): User? {
    return transaction {
        User.findById(username)
    }
}

fun getSaltedPassword(passwordPlaintext: String, salt: String) = getDigestFunction("SHA-256") { salt }.invoke(passwordPlaintext).joinToString(", ")

fun generateSalt(): String = RandomStringUtils.randomAlphanumeric(16)