package com.adamratzman.utils

import org.jetbrains.exposed.dao.Entity

interface ConvertableToFrontendObject<T, K> {
    fun toFrontendObject(): K
}

interface UpdateableWithFrontendObject<T : Entity<*>, K> : ConvertableToFrontendObject<T, K> {
    fun getMutatingFunction(): T.(K) -> Unit

    @Suppress("UNCHECKED_CAST")
    fun mutate(frontendObject: K) = getMutatingFunction().invoke((this as T), frontendObject)
}