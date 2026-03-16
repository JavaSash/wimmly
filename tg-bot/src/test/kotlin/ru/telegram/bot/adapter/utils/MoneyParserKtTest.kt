package ru.telegram.bot.adapter.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import java.math.BigDecimal
import java.math.RoundingMode

class MoneyParserKtTest {

    @ParameterizedTest
    @ValueSource(strings = [
        "1234.56",
        "0.00",
        "-1234.56"
    ])
    fun `formatMoney - should format amount`(stringAmount: String) {
        val amount = BigDecimal(stringAmount)
        val result = amount.formatMoney()
        assertEquals("$stringAmount ₽", result)
    }

    @Test
    fun `formatMoney - should round to two decimals`() {
        val amount = BigDecimal("1234.5678")
        val result = amount.formatMoney()
        assertEquals("1234.57 ₽", result)
    }

    @Test
    fun `parseAmount - should parse valid amount with space`() {
        val result = parseAmount("1 234.56")
        assertAll(
            { assertEquals(BigDecimal("1234.56").setScale(2, RoundingMode.HALF_UP), result) },
            { assertEquals(2, result.scale()) }
        )
    }

    @Test
    fun `parseAmount - should parse valid amount with comma`() {
        val result = parseAmount("1234,56")
        assertAll(
            { assertEquals(BigDecimal("1234.56").setScale(2, RoundingMode.HALF_UP), result) },
            { assertEquals(2, result.scale()) }
        )
    }

    @Test
    fun `parseAmount - should parse valid amount with spaces and comma`() {
        val result = parseAmount("1 234,56")
        assertAll(
            { assertEquals(BigDecimal("1234.56").setScale(2, RoundingMode.HALF_UP), result) },
            { assertEquals(2, result.scale()) }
        )
    }

    @Test
    fun `parseAmount - should parse integer amount`() {
        val result = parseAmount("1000")
        assertAll(
            { assertEquals(BigDecimal("1000.00").setScale(2, RoundingMode.HALF_UP), result) },
            { assertEquals(2, result.scale()) }
        )
    }

    @Test
    fun `parseAmount - should parse amount with leading zeros`() {
        val result = parseAmount("00123.45")
        assertAll(
            { assertEquals(BigDecimal("123.45").setScale(2, RoundingMode.HALF_UP), result) },
            { assertEquals(2, result.scale()) }
        )
    }

    @Test
    fun `parseAmount - should round to two decimals`() {
        val result = parseAmount("1234.5678")
        assertAll(
            { assertEquals(BigDecimal("1234.57").setScale(2, RoundingMode.HALF_UP), result) },
            { assertEquals(2, result.scale()) }
        )
    }

    @Test
    fun `parseAmount - should throw for null input`() {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount(null)
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @Test
    fun `parseAmount - should throw for blank input`() {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount("   ")
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @Test
    fun `parseAmount - should throw for empty input`() {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount("")
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @Test
    fun `parseAmount - should throw for invalid format`() {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount("abc")
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @Test
    fun `parseAmount - should throw for amount with multiple dots`() {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount("123.45.67")
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @Test
    fun `parseAmount - should throw for amount with letters`() {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount("123a.45")
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "0",
        "-100",
        "-0.00"
    ])
    fun `parseAmount - should throw for more than zero amount`(amount: String) {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount(amount)
        }
        assertEquals("Сумма должна быть больше нуля", exception.message)
    }
}