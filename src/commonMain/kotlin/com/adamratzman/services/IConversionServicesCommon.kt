package com.adamratzman.services

import kotlinx.serialization.Serializable
import io.kvision.annotations.KVService

@KVService
interface IBaseConversionService {
    suspend fun convert(baseFrom: Int, baseTo: Int, numberAsString: String): String
    suspend fun getBasesRange(): BaseConversionRange
}

@Serializable
data class BaseConversionRange(val low: Int, val high: Int)