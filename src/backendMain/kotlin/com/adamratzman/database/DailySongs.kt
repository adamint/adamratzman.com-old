package com.adamratzman.database

import com.adamratzman.services.DailySong
import com.adamratzman.services.SerializableDate
import com.adamratzman.utils.UpdateableWithFrontendObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object DailySongs : IdTable<Int>() {
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

    override val id = integer("id").autoIncrement().entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class DailySongEntity(id: EntityID<Int>) : Entity<Int>(id), UpdateableWithFrontendObject<DailySongEntity, DailySong> {
    companion object : EntityClass<Int, DailySongEntity>(DailySongs)

    var year by DailySongs.year
    var month by DailySongs.month
    var dayOfMonth by DailySongs.dayOfMonth
    var trackId by DailySongs.trackId
    var note by DailySongs.note
    var protectedNote by DailySongs.protectedNote
    var protectedNotePassword by DailySongs.protectedNotePassword
    var artistsJson by DailySongs.artistsJson
    var trackName by DailySongs.trackName
    var genresJson by DailySongs.genresJson
    var imageUrl by DailySongs.imageUrl

    override fun toFrontendObject(): DailySong = DailySong(
        date = SerializableDate(year, month, dayOfMonth),
        trackId = trackId,
        note = note,
        protectedNote = protectedNote,
        protectedNotePassword = protectedNotePassword,
        artists = Json.decodeFromString(artistsJson),
        trackName = trackName,
        genres = genresJson?.let { Json.decodeFromString(it) },
        imageUrl = imageUrl
    )

    override fun getMutatingFunction(): DailySongEntity.(DailySong) -> Unit = { dailySong ->
        this.year = dailySong.date.year
        this.month = dailySong.date.monthNumber
        this.dayOfMonth = dailySong.date.dayOfMonth
        this.trackId = dailySong.trackId
        this.note = dailySong.note
        this.protectedNote = dailySong.protectedNote
        this.protectedNotePassword = dailySong.protectedNotePassword
        this.artistsJson = Json.encodeToString(dailySong.artists)
        this.trackName = dailySong.trackName
        this.genresJson = Json.encodeToString(dailySong.genres)
        this.imageUrl = dailySong.imageUrl
    }
}