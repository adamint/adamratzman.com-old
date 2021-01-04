package com.adamratzman.services

import com.adamratzman.utils.fixServiceRoutingWithOptionalBeforeSending

object DailySongServiceFrontend  {
    private val dailySongService = DailySongService(beforeSend = fixServiceRoutingWithOptionalBeforeSending())

    suspend fun getAllDays(): List<DailySong> = dailySongService.getAllDays()
    suspend fun getDay(date: SerializableDate): DailySong = dailySongService.getDay(date)
    suspend fun addOrUpdate(dailySong: DailySong): Boolean = dailySongService.addOrUpdate(dailySong)
    suspend fun deleteDay(date: SerializableDate): Boolean = dailySongService.deleteDay(date)
}