package com.adamratzman.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

val dbUserEnvVariable: String = System.getenv("DB_USER")
val dbPassEnvVariable: String = System.getenv("DB_PASS")
val jdbcConnectionUrl: String = System.getenv("JDBC_CONNECTION_URL")

object SiteDatabase {
    fun initialize() {
        Database.connect(
            jdbcConnectionUrl,
            if (jdbcConnectionUrl.contains("sqlserver")) "com.microsoft.sqlserver.jdbc.SQLServerDriver" else "com.mysql.cj.jdbc.Driver",
            dbUserEnvVariable,
            dbPassEnvVariable
        )

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                ShortenedUrls, Users, DailySongs
            )
            SchemaUtils.createMissingTablesAndColumns(
                BlogPosts
            )
            SchemaUtils.createMissingTablesAndColumns(
                BlogPostComments
            )
            SchemaUtils.createMissingTablesAndColumns(
                BlogPostCategories
            )
        }
    }
}