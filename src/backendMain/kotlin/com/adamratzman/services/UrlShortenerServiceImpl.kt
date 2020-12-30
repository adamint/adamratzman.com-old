package com.adamratzman.services

import com.adamratzman.database.ShortenedUrls
import com.adamratzman.database.addTo
import com.adamratzman.database.toShortenedUrl
import org.apache.commons.validator.routines.UrlValidator
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.treksoft.kvision.remote.ServiceException

actual class UrlShortenerService : IUrlShortenerService {
    override suspend fun insertShortenedUrl(shortenedUrlDto: ShortenedUrlDto): ShortenedUrl {
        return transaction {
            shortenedUrlDto.path?.let { path ->
                if (ShortenedUrls.select { ShortenedUrls.path.eq(path) }.count() != 0L) {
                    throw ServiceException("There's already a URL generated with this path.")
                }
                if (path.length > shortenedUrlPathMaxLength) {
                    throw ServiceException("The provided path must be between 1 and $shortenedUrlPathMaxLength alphanumeric characters")
                }
                if (path.any { !it.isAlphanumeric() }) throw ServiceException("The provided path must be between 1 and $shortenedUrlPathMaxLength alphanumeric characters")
            }
            if (!UrlValidator.getInstance().isValid(shortenedUrlDto.url)) {
                throw ServiceException("Please provide a valid URL.")
            }

            val shortenedPath = if (shortenedUrlDto.path == null) {
                val existingPaths = ShortenedUrls.slice(ShortenedUrls.path).selectAll().map { it[ShortenedUrls.path] }
                generatePath(existingPaths, length = 4)
            } else shortenedUrlDto.path

            val shortenedUrl = ShortenedUrl(
                    shortenedUrlDto.url,
                    shortenedPath,
                    shortenedUrlDto.rickrollAllowed)

            ShortenedUrls.insert {
                shortenedUrl addTo it
            }
            shortenedUrl
        }
    }

    override suspend fun getShortenedUrls(): List<ShortenedUrl> {
        return transaction {
            ShortenedUrls.selectAll().map { it.toShortenedUrl() }
        }
    }

    override suspend fun getShortenedUrl(path: String): ShortenedUrl {
        return transaction {
            val result = ShortenedUrls
                    .select { ShortenedUrls.path.eq(path) }
            if (result.count() == 0L) throw ServiceException("Shortened URL path was not found")
            result.first().toShortenedUrl()
        }
    }
}

private fun generatePath(existingPaths: List<String>, length: Int, characters: List<Char> = ('a'..'z').toList()): String {
    val charRestrictions = existingPaths
            .map { string -> string.mapIndexed { index, char -> index to char } }
            .flatten()
            .groupBy { it.first }
            .map { it.key to it.value.map { pair -> pair.second } }
            .toMap()

    return (0 until length).map { index ->
        val allowedChars =
                if (index in charRestrictions) characters - charRestrictions.getValue(index)
                else characters
        allowedChars.random()
    }.joinToString("")
}