package com.adamratzman.utils

infix fun <T> T.plusList(list: List<T>) = listOf(this) + list

fun String.asDuplicatedPair() = this to this