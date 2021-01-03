package com.adamratzman.services

import com.adamratzman.utils.fixServiceRoutingWithOptionalBeforeSending

object CalculatorServiceFrontend {
    private val calculatorService = CalculatorService(beforeSend = fixServiceRoutingWithOptionalBeforeSending())

    suspend fun getCalculatorFunctions() = calculatorService.getCalculatorFunctions()
        .groupBy { it.type }
        .map { (type, mathFunctions) -> type to mathFunctions.sortedBy { it.token } }
        .toMap()

    suspend fun getCalculatorConstants(precision: Int, radix: Int) = calculatorService.getCalculatorConstants(precision, radix)
    suspend fun calculate(input: String, precision: Int, radix: Int, useRadians: Boolean) = calculatorService.calculate(input, precision, radix, useRadians)
}