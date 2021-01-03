package com.adamratzman.services

import com.adamratzman.utils.fixServiceRoutingWithOptionalBeforeSending

object UrlShortenerServiceFrontend {
    private val urlShortenerService = UrlShortenerService(beforeSend = fixServiceRoutingWithOptionalBeforeSending())

    suspend fun insertShortenedUrl(shortenedUrlDto: ShortenedUrlDto): ShortenedUrl {
        return urlShortenerService.insertShortenedUrl(shortenedUrlDto)
    }

    suspend fun getShortenedUrls() = urlShortenerService.getShortenedUrls()
    suspend fun getShortenedUrl(path: String) = urlShortenerService.getShortenedUrl(path)
}