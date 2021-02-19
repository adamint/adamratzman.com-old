package com.adamratzman.services

import com.adamratzman.math.Expression
import com.adamratzman.math.parser.ExpressionTokenizer
import com.adamratzman.math.utils.round
import pl.treksoft.kvision.remote.ServiceException
import java.math.MathContext

actual class CalculatorService : ICalculatorService {
    override suspend fun getCalculatorFunctions(): List<MathFunction> {
        val tokenizer = ExpressionTokenizer(10, MathContext(defaultCalculatorPrecision))
        return tokenizer.allMathFunctions
                .map { function ->
                    MathFunction(
                            function.token,
                            function.aliases,
                            MathFunctionGroup(function.type.name.toLowerCase()),
                            function.parameters.map { it.paramString }
                    )
                }
    }

    override suspend fun getCalculatorConstants(precision: Int, radix: Int): List<MathConstant> {
        val tokenizer = ExpressionTokenizer(radix, MathContext(precision))
        return tokenizer.constants
                .sortedBy { it.name }
                .map { constant -> MathConstant(constant.name, constant.generate(tokenizer).round(precision).stripTrailingZeros()) }
    }

    override suspend fun calculate(input: String, precision: Int, radix: Int, useRadians: Boolean): String {
        if (input.isEmpty()) throw ServiceException("The input was empty")
        if (precision !in 1..1000) throw ServiceException("Precision has to be between 1 and 1,000")
        if (radix !in 2..36) throw ServiceException("Radix has to be between 2 and 36")

        val mathParser = Expression(input, useRadians, 10, MathContext(precision))
        return try {
            val result = mathParser.evaluate().round(precision).stripTrailingZeros()
            if (radix == 10) result.toPlainString() else result.toLong().toString(radix)
        } catch (exception: Exception) {
            val exceptionMessage = (exception.localizedMessage
                    ?: exception.message)?.split("Exception:")?.last()?.trim()
                    ?: "There was an issue with your expression. Recheck the syntax, or make it less complex"
            throw ServiceException(exceptionMessage)
        }
    }
}