package com.adamratzman.services

import pl.treksoft.kvision.remote.ServiceException

actual open class BaseConversionService : IBaseConversionService {
    override suspend fun convert(baseFrom: Int, baseTo: Int, numberAsString: String): String {
        return try {
            numberAsString.toLong(baseFrom).toString(baseTo)
        } catch (ignored: Exception) {
            throw ServiceException("Invalid base or number supplied.")
        }
    }

    override suspend fun getBasesRange(): BaseConversionRange {
        return BaseConversionRange(2, 36)
    }
}