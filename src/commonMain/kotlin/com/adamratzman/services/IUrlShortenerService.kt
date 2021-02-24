package com.adamratzman.services

import kotlinx.serialization.Serializable
import pl.treksoft.kvision.annotations.KVService

const val shortenedUrlPathMaxLength: Int = 50

@KVService
interface IUrlShortenerService {
    suspend fun insertShortenedUrl(shortenedUrlDto: ShortenedUrlDto): ShortenedUrl
    suspend fun getShortenedUrls(): List<ShortenedUrl>
    suspend fun getShortenedUrl(path: String): ShortenedUrl
}

fun Char.isAlphanumeric() = this in '0'..'9' || this in 'a'..'z' || this in 'A'..'Z'

@Serializable
data class ShortenedUrl(
        val url: String,
        val path: String,
        val rickrollAllowed: Boolean
)

@Serializable
data class ShortenedUrlDto(
        val url: String,
        val path: String? = null,
        val rickrollAllowed: Boolean
)