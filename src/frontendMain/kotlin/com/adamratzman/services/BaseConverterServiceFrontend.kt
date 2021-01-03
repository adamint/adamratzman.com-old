package com.adamratzman.services

import com.adamratzman.utils.fixServiceRoutingWithOptionalBeforeSending

object BaseConverterServiceFrontend {
    private val baseConversionService = BaseConversionService(beforeSend = fixServiceRoutingWithOptionalBeforeSending())

    suspend fun convert(baseFrom: Int, baseTo: Int, numberAsString: String): String {
        return baseConversionService.convert(baseFrom, baseTo, numberAsString)
    }

    suspend fun getBasesRange() = baseConversionService.getBasesRange()
}