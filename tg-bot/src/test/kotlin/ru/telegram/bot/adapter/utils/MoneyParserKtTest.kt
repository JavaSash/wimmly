package ru.telegram.bot.adapter.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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

    @ParameterizedTest
    @CsvSource(
        "0, 0.00",
        "500, 500.00",
        "-999.99, -999.99",
        "1000, 1\u00A0000.00",
        "999.995, 1\u00A0000.00",
        "-12345.675, -12\u00A0345.68",
        "1234455, 1\u00A0234\u00A0455.00",
        "9007199254740993.12, 9\u00A0007\u00A0199\u00A0254\u00A0740\u00A0993.12",
    )
    fun `formatMoney should group thousands without losing precision`(input: String, expected: String) {
        assertEquals("$expected ₽", input.toBigDecimal().formatMoney(groupThousands = true))
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

    @ParameterizedTest
    @ValueSource(strings = [
        "abc", "100 рублей", "100 +", "100 ** 2", "(100 + 200", "100 + 200)", ")100(",
        "100 + ()", "10.5.2 + 100", "1,2.3", "2^3", "100 + 20%", "1 234.56",
        "1 234,56", "12 34", "2(3+4)", "1e3",
    ])
    fun `parseAmount - should throw for invalid format`(amount: String) {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount(amount)
        }
        assertEquals(BotErrors.INVALID_AMOUNT.msg, exception.message)
    }

    @ParameterizedTest
    @ValueSource(strings = [
        "0",
        "-100",
        "-0.00",
        "1 / 1000",
        "5 - 10",
    ])
    fun `parseAmount - should throw for more than zero amount`(amount: String) {
        val exception = assertThrows<InvalidAmountException> {
            parseAmount(amount)
        }
        assertEquals(BotErrors.AMOUNT_SHOULD_BE_POSITIVE.msg, exception.message)
    }

    @ParameterizedTest
    @CsvSource(
        "' 100 + 250 + 50 ', 400.00",
        "1000 - 250 - 50, 700.00",
        "150 * 3, 450.00",
        "1000 / 4 / 2, 125.00",
        "1000 \\ 4, 250.00",
        "1000 : 4, 250.00",
        "1+2*5/4, 3.50",
        "100 + 200 * 3, 700.00",
        "((100 + 200) * 3) / 2, 450.00",
        "'100,50 + 200.25', 300.75",
        "100 - (-50), 150.00",
        "+100 + -(-50), 150.00",
        "1 / 3 * 10, 3.33",
        "1 / 8, 0.13",
        "123.999, 124.00",
        "99999999999999999.99, 99999999999999999.99",
    )
    fun `parseAmount should evaluate expressions and round only the final amount`(input: String, expected: String) {
        assertEquals(expected.toBigDecimal(), parseAmount(input))
    }

    @ParameterizedTest
    @ValueSource(strings = ["1 / 0", "1 \\ -0", "1 : (2 - 2)"])
    fun `parseAmount should report division by zero`(input: String) {
        assertEquals(
            BotErrors.AMOUNT_DIVISION_BY_ZERO.msg,
            assertThrows<InvalidAmountException> { parseAmount(input) }.message
        )
    }

    @Test
    fun `parseAmount should enforce expression limits`() {
        val maxDepth = "(".repeat(10) + "1" + ")".repeat(10)
        assertEquals(BigDecimal("1.00"), parseAmount(maxDepth))
        assertEquals(BigDecimal("1.00"), parseAmount(" ".repeat(255) + "1"))
        assertEquals(BigDecimal("1.00"), parseAmount("0".repeat(31) + "1"))

        listOf("($maxDepth)", " ".repeat(256) + "1", "0".repeat(32) + "1").forEach { input ->
            assertEquals(
                BotErrors.AMOUNT_EXPRESSION_TOO_COMPLEX.msg,
                assertThrows<InvalidAmountException> { parseAmount(input) }.message
            )
        }
    }

    @Test
    fun `parseAmount should reject results beyond the storage limit after rounding`() {
        val error = assertThrows<InvalidAmountException> {
            parseAmount("99999999999999999.99 + 0.005")
        }
        assertEquals(BotErrors.AMOUNT_SHOULD_BE_SMALLER.msg, error.message)
    }
}
