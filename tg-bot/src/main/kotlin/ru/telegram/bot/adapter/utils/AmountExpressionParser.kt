package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

internal class AmountExpressionParser(private val input: String) {
    private companion object {
        const val MAX_LENGTH = 256
        const val MAX_DEPTH = 10
        const val MAX_OPERAND_DIGITS = 32
        val divisionContext = MathContext(34, RoundingMode.HALF_UP)
    }

    private var position = 0

    /**
     * Evaluates the entire input and returns the result before rounding to cents.
     *
     * @throws InvalidAmountException if the input is invalid, exceeds a parser limit, or divides by zero.
     */
    fun evaluate(): BigDecimal {
        if (input.length > MAX_LENGTH) invalid(BotErrors.AMOUNT_EXPRESSION_TOO_COMPLEX)
        val result = expression(0)
        if (peek() != null) invalid()
        return result
    }

    /** Evaluates addition and subtraction from left to right at the given parenthesis [depth]. */
    private fun expression(depth: Int): BigDecimal {
        var value = term(depth)
        while (true) {
            val operator = peek()
            if (operator != '+' && operator != '-') return value
            position++
            val right = term(depth)
            value = if (operator == '+') value + right else value - right
        }
    }

    /** Evaluates multiplication and division from left to right at the given parenthesis [depth]. */
    private fun term(depth: Int): BigDecimal {
        var value = operand(depth)
        while (true) {
            val operator = peek()
            when (operator) {
                '*', '/', '\\', ':' -> position++
                else -> return value
            }
            val right = operand(depth)
            value = if (operator == '*') value * right else divide(value, right)
        }
    }

    /** Applies unary signs to a number or a parenthesized expression, enforcing the nesting limit at [depth]. */
    private fun operand(depth: Int): BigDecimal {
        var negative = false
        while (true) {
            val sign = peek()
            if (sign != '+' && sign != '-') break
            if (sign == '-') negative = !negative
            position++
        }

        val value = if (peek() == '(') {
            if (depth >= MAX_DEPTH) invalid(BotErrors.AMOUNT_EXPRESSION_TOO_COMPLEX)
            position++
            val nested = expression(depth + 1)
            if (peek() != ')') invalid()
            position++
            nested
        } else {
            number()
        }
        return if (negative) value.negate() else value
    }

    /** Reads a numeric literal with an optional decimal point or comma and enforces the digit limit. */
    private fun number(): BigDecimal {
        val start = position
        var digits = readDigits()
        if (input.getOrNull(position) == '.' || input.getOrNull(position) == ',') {
            position++
            digits += readDigits()
        }
        if (digits == 0) invalid()
        if (digits > MAX_OPERAND_DIGITS) invalid(BotErrors.AMOUNT_EXPRESSION_TOO_COMPLEX)
        return input.substring(start, position).replace(',', '.').toBigDecimal()
    }

    /** Advances past consecutive ASCII digits and returns how many were read. */
    private fun readDigits(): Int {
        val start = position
        while (position < input.length && input[position] in '0'..'9') position++
        return position - start
    }

    /**
     * Divides [dividend] by [divisor] exactly when possible and rounds repeating decimals to
     * 34 significant digits using [RoundingMode.HALF_UP].
     *
     * @throws InvalidAmountException if [divisor] is zero.
     */
    private fun divide(dividend: BigDecimal, divisor: BigDecimal): BigDecimal {
        if (divisor.signum() == 0) invalid(BotErrors.AMOUNT_DIVISION_BY_ZERO)
        return try {
            dividend.divide(divisor)
        } catch (_: ArithmeticException) {
            dividend.divide(divisor, divisionContext)
        }
    }

    /** Skips whitespace and returns the next character without consuming it, or null at the end. */
    private fun peek(): Char? {
        while (input.getOrNull(position)?.isWhitespace() == true) position++
        return input.getOrNull(position)
    }

    /** Throws [InvalidAmountException] with the user-facing message for [error]. */
    private fun invalid(error: BotErrors = BotErrors.INVALID_AMOUNT): Nothing =
        throw InvalidAmountException(error.msg)
}
