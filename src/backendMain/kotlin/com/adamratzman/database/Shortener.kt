package com.adamratzman.database

import com.adamratzman.services.ShortenedUrl
import com.adamratzman.services.shortenedUrlPathMaxLength
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement

object ShortenedUrls : Table() {
    val url = text("url")
    val path = varchar("path", shortenedUrlPathMaxLength)
    val rickrollAllowed = bool("rickroll_allowed")

    override val primaryKey = PrimaryKey(path)
}

fun ResultRow.toShortenedUrl() = ShortenedUrl(
        this[ShortenedUrls.url],
        this[ShortenedUrls.path],
        this[ShortenedUrls.rickrollAllowed]
)

infix fun <T: Any> ShortenedUrl.addTo(insertStatement: InsertStatement<T>)  = insertStatement.apply {
    this[ShortenedUrls.url] = url
    this[ShortenedUrls.path] = path
    this[ShortenedUrls.rickrollAllowed] = rickrollAllowed
}
