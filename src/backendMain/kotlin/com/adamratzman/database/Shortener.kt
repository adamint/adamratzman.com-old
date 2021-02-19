package com.adamratzman.database

import com.adamratzman.services.ShortenedUrl
import com.adamratzman.services.shortenedUrlPathMaxLength
import com.adamratzman.utils.UpdateableWithFrontendObject
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object ShortenedUrls : IdTable<String>() {
    val url = text("url")
    val rickrollAllowed = bool("rickroll_allowed")

    override val id = varchar("path", shortenedUrlPathMaxLength).entityId()
    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class ShortenedUrlEntity(id: EntityID<String>) : Entity<String>(id), UpdateableWithFrontendObject<ShortenedUrlEntity, ShortenedUrl> {
    companion object : EntityClass<String, ShortenedUrlEntity>(ShortenedUrls)

    var url by ShortenedUrls.url
    var path by ShortenedUrls.id
    var rickrollAllowed by ShortenedUrls.rickrollAllowed

    override fun toFrontendObject(): ShortenedUrl = ShortenedUrl(url, path.value, rickrollAllowed)

    override fun getMutatingFunction(): ShortenedUrlEntity.(ShortenedUrl) -> Unit = { shortenedUrl ->
        this.url = shortenedUrl.url
        this.rickrollAllowed = rickrollAllowed
    }
}