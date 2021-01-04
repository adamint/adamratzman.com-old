package com.adamratzman.services

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.annotations.KVService
import pl.treksoft.kvision.types.Decimal

const val defaultCalculatorPrecision = 10

@KVService
interface ICalculatorService {
    suspend fun getCalculatorFunctions(): List<MathFunction>
    suspend fun getCalculatorConstants(precision: Int, radix: Int): List<MathConstant>
    suspend fun calculate(input: String, precision: Int, radix: Int, useRadians: Boolean): String
}

@Serializable
data class MathConstant(
        val name: String,
        @Contextual val value: Decimal
)

@Serializable
data class MathFunctionGroup(
        val name: String
)

@Serializable
data class MathFunction(
        val token: String,
        val aliases: List<String>,
        val type: MathFunctionGroup,
        val functionOverloadStrings: List<String>
)