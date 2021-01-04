package com.adamratzman.database

import com.adamratzman.services.DailySong
import com.adamratzman.services.SerializableDate
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement

object DailySongs : Table() {
    val id = integer("id").autoIncrement()
    val year = integer("year")
    val month = integer("month")
    val dayOfMonth = integer("dayOfMonth")
    val trackId = varchar("trackId", 128)
    val note = text("note").nullable()
    val protectedNote = text("protectedNote").nullable()
    val protectedNotePassword = varchar("protectedNotePassword", 16)
    val artistsJson = text("artistsJson")
    val trackName = text("trackName")
    val genresJson = text("genresJson").nullable()
    val imageUrl = varchar("imageUrl", 350).nullable()

    override val primaryKey = PrimaryKey(id)
}

fun ResultRow.toDailySong() = DailySong(
    date = SerializableDate(this[DailySongs.year], this[DailySongs.month], this[DailySongs.dayOfMonth]),
    trackId = this[DailySongs.trackId],
    note = this[DailySongs.note],
    protectedNote = this[DailySongs.protectedNote],
    protectedNotePassword = this[DailySongs.protectedNotePassword],
    artists = Json.decodeFromString(this[DailySongs.artistsJson]),
    trackName = this[DailySongs.trackName],
    genres = this[DailySongs.genresJson]?.let { Json.decodeFromString(it) },
    imageUrl = this[DailySongs.imageUrl]
)

infix fun <T : Any> DailySong.addTo(insertStatement: InsertStatement<T>) = insertStatement.apply {
    this[DailySongs.year] = date.year
    this[DailySongs.month] = date.monthNumber
    this[DailySongs.dayOfMonth] = date.dayOfMonth
    this[DailySongs.trackId] = trackId
    this[DailySongs.note] = note
    this[DailySongs.protectedNote] = protectedNote
    this[DailySongs.protectedNotePassword] = protectedNotePassword
    this[DailySongs.artistsJson] = Json.encodeToString(artists)
    this[DailySongs.trackName] = trackName
    this[DailySongs.genresJson] = Json.encodeToString(genres)
    this[DailySongs.imageUrl] = imageUrl
}

infix fun DailySong.updateTo(updateStatement: UpdateStatement) = updateStatement.apply {
    this[DailySongs.year] = date.year
    this[DailySongs.month] = date.monthNumber
    this[DailySongs.dayOfMonth] = date.dayOfMonth
    this[DailySongs.trackId] = trackId
    this[DailySongs.note] = note
    this[DailySongs.protectedNote] = protectedNote
    this[DailySongs.protectedNotePassword] = protectedNotePassword
    this[DailySongs.artistsJson] = Json.encodeToString(artists)
    this[DailySongs.trackName] = trackName
    this[DailySongs.genresJson] = Json.encodeToString(genres)
    this[DailySongs.imageUrl] = imageUrl
}