package com.adamratzman.services

import com.adamratzman.database.*
import com.google.inject.Inject
import io.ktor.application.ApplicationCall
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import pl.treksoft.kvision.remote.ServiceException

actual class DailySongService : IDailySongService {
    @Inject
    lateinit var call: ApplicationCall

    private fun getDailySongForDay(date: SerializableDate): DailySong? = transaction {
        DailySongs.select {
            DailySongs.year.eq(date.year) and DailySongs.month.eq(date.monthNumber) and
                    DailySongs.dayOfMonth.eq(date.dayOfMonth)
        }.firstOrNull()?.toDailySong()
    }

    override suspend fun getAllDays(): List<DailySong> {
        return transaction {
            DailySongs.selectAll().map { it.toDailySong() }
        }
    }

    override suspend fun getDay(date: SerializableDate): DailySong {
        return getDailySongForDay(date) ?: throw ServiceException("A song with this date was found.")
    }

    override suspend fun deleteDay(date: SerializableDate): Boolean {
        if (call.sessions.get<UserPrincipal>()?.role != UserRole.ADMIN) return false
        if (getDailySongForDay(date) != null) {
            transaction {
                DailySongs.deleteWhere {
                    DailySongs.year eq date.year and DailySongs.month.eq(date.monthNumber) and
                            DailySongs.dayOfMonth.eq(date.dayOfMonth)
                }
            }
        }
        return true
    }

    override suspend fun addOrUpdate(dailySong: DailySong): Boolean {
        if (call.sessions.get<UserPrincipal>()?.role != UserRole.ADMIN) return false
        val date = dailySong.date
        if (getDailySongForDay(date) != null) {
            transaction {
                DailySongs.update({
                    DailySongs.year.eq(date.year) and DailySongs.month.eq(date.monthNumber) and
                            DailySongs.dayOfMonth.eq(date.dayOfMonth)
                }) { dailySong updateTo it }
            }
        } else {
            transaction {
                DailySongs.insert {
                    dailySong addTo it
                }
            }
        }

        return true
    }
}