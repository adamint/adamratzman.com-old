package com.adamratzman.services

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.annotations.KVService

@KVService
interface IDailySongService {
    suspend fun getAllDays(): List<DailySong>
    suspend fun getDay(date: SerializableDate): DailySong
    suspend fun addOrUpdate(dailySong: DailySong): Boolean
    suspend fun deleteDay(date: SerializableDate): Boolean
}

@Serializable
data class DailySong(
    val date: SerializableDate,
    val trackId: String,
    val note: String? = null,
    val protectedNote: String? = null,
    val protectedNotePassword: String,
    val artists: List<ArtistNameAndSpotifyId>,
    val trackName: String,
    val genres: List<String>? = null,
    val imageUrl: String? = null
)

@Serializable
data class ArtistNameAndSpotifyId(
    val name: String,
    val spotifyId: String
)

@Serializable
data class SerializableDate(
    val year: Int,
    val monthNumber: Int,
    val dayOfMonth: Int
) {
    fun toLocalDate() = LocalDate(year, monthNumber, dayOfMonth)
}