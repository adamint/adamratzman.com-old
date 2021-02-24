package com.adamratzman.services

import com.adamratzman.database.ShortenedUrlEntity
import com.adamratzman.database.ShortenedUrls
import org.apache.commons.validator.routines.UrlValidator
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import pl.treksoft.kvision.remote.ServiceException

actual class UrlShortenerService : IUrlShortenerService {
    override suspend fun insertShortenedUrl(shortenedUrlDto: ShortenedUrlDto): ShortenedUrl {
        return transaction {
            shortenedUrlDto.path?.let { path ->
                if (ShortenedUrlEntity.findById(path) != null) {
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
                val existingPaths = ShortenedUrls.slice(ShortenedUrls.id).selectAll().map { it[ShortenedUrls.id].value }
                generatePath(existingPaths, length = 4)
            } else shortenedUrlDto.path

            val shortenedUrl = ShortenedUrl(
                shortenedUrlDto.url,
                shortenedPath,
                shortenedUrlDto.rickrollAllowed
            )

            ShortenedUrlEntity.new(shortenedUrl.path) {
                this.mutate(shortenedUrl)
            }
            shortenedUrl
        }
    }

    override suspend fun getShortenedUrls(): List<ShortenedUrl> {
        return transaction {
            ShortenedUrlEntity.all().toList().map { it.toFrontendObject() }
        }
    }

    override suspend fun getShortenedUrl(path: String): ShortenedUrl {
        return transaction {
            ShortenedUrlEntity.findById(path)?.toFrontendObject() ?: throw ServiceException("Shortened URL path was not found")
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