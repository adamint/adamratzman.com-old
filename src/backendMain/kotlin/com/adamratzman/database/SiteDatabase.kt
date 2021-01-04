package com.adamratzman.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val dbUserEnvVariable: String = System.getenv("DB_USER")
val dbPassEnvVariable: String = System.getenv("DB_PASS")
val dbUrlAndPort: String = System.getenv("DB_URL_WITH_PORT")

object SiteDatabase {
    fun initialize() {
        Database.connect(
                "jdbc:mysql://$dbUrlAndPort/site",
                "com.mysql.cj.jdbc.Driver",
                dbUserEnvVariable,
                dbPassEnvVariable
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(ShortenedUrls, Users, DailySongs)
        }
    }
}