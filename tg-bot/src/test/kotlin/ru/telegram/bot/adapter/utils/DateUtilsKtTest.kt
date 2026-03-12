package ru.telegram.bot.adapter.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import ru.telegram.bot.adapter.exceptions.InvalidDateException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.utils.Constants.Errors.END_BEFORE_START_DATE
import ru.telegram.bot.adapter.utils.Constants.Errors.FUTURE_DATE
import ru.telegram.bot.adapter.utils.Constants.Errors.NOT_UNIX_DATE
import java.time.*
import java.time.format.DateTimeFormatter

class DateUtilsKtTest {
    @Test
    fun `validateDate with LocalDateTime - should accept current date`() {
        val date = LocalDateTime.now()
        validateDate(date)
    }

    @Test
    fun `validateDate with LocalDateTime - should throw for future date`() {
        val futureDate = LocalDateTime.now().plusDays(1)
        val exception = assertThrows<InvalidDateException> {
            validateDate(futureDate)
        }
        assertEquals(FUTURE_DATE, exception.message)
    }

    @Test
    fun `validateDate with LocalDateTime - should throw for date before 1970`() {
        val oldDate = LocalDateTime.of(1969, 12, 31, 23, 59)
        val exception = assertThrows<InvalidDateException> {
            validateDate(oldDate)
        }
        assertEquals(NOT_UNIX_DATE, exception.message)
    }

    @Test
    fun `validateDate with Instant - should accept current date`() {
        val date = Instant.now()
        validateDate(date)
    }

    @Test
    fun `validateDate with Instant - should throw for future date`() {
        val futureDate = Instant.now().plus(Duration.ofDays(1))
        val exception = assertThrows<InvalidDateException> {
            validateDate(futureDate)
        }
        assertEquals(FUTURE_DATE, exception.message)
    }

    @Test
    fun `validateDate with Instant - should throw for date before 1970`() {
        val oldDate = ZonedDateTime.of(1969, 12, 31, 23, 59, 0, 0, ZoneId.systemDefault()).toInstant()
        val exception = assertThrows<InvalidDateException> {
            validateDate(oldDate)
        }
        assertEquals(NOT_UNIX_DATE, exception.message)
    }

    @Test
    fun `validateDate with ZonedDateTime - should accept current date`() {
        val date = ZonedDateTime.now()
        validateDate(date)
    }

    @Test
    fun `validateDate with ZonedDateTime - should throw for future date`() {
        val futureDate = ZonedDateTime.now().plusDays(1)
        val exception = assertThrows<InvalidDateException> {
            validateDate(futureDate)
        }
        assertEquals(FUTURE_DATE, exception.message)
    }

    @Test
    fun `validateDate with ZonedDateTime - should throw for date before 1970`() {
        val oldDate = ZonedDateTime.of(1969, 12, 31, 23, 59, 0, 0, ZoneOffset.UTC)
        val exception = assertThrows<InvalidDateException> {
            validateDate(oldDate)
        }
        assertEquals(NOT_UNIX_DATE, exception.message)
    }

    @Test
    fun `validatePeriod with LocalDateTime - should accept valid period`() {
        val from = LocalDateTime.now().minusDays(5)
        val to = LocalDateTime.now()
        validatePeriod(from, to)
    }

    @Test
    fun `validatePeriod with LocalDateTime - should throw when end before start`() {
        val from = LocalDateTime.now()
        val to = LocalDateTime.now().minusDays(1)
        val exception = assertThrows<InvalidDateException> {
            validatePeriod(from, to)
        }
        assertEquals(END_BEFORE_START_DATE, exception.message)
    }

    @Test
    fun `validatePeriod with LocalDateTime - should validate both dates`() {
        val from = LocalDateTime.now()
        val to = LocalDateTime.now().plusDays(2)
        val exception = assertThrows<InvalidDateException> {
            validatePeriod(from, to)
        }
        assertEquals(FUTURE_DATE, exception.message)
    }

    @Test
    fun `validatePeriod with ZonedDateTime - should accept valid period`() {
        val from = ZonedDateTime.now().minusDays(5)
        val to = ZonedDateTime.now()
        validatePeriod(from, to)
    }

    @Test
    fun `validatePeriod with ZonedDateTime - should throw when end before start`() {
        val from = ZonedDateTime.now()
        val to = ZonedDateTime.now().minusDays(1)
        val exception = assertThrows<InvalidDateException> {
            validatePeriod(from, to)
        }
        assertEquals(END_BEFORE_START_DATE, exception.message)
    }

    @Test
    fun `toLocalDateTime - should convert Instant to LocalDateTime`() {
        val instant = Instant.parse("2024-03-15T10:30:00Z")
        val result = instant.atZone(ZoneOffset.UTC).toLocalDateTime()

        assertAll(
            { assertEquals(2024, result.year) },
            { assertEquals(3, result.monthValue) },
            { assertEquals(15, result.dayOfMonth) },
            { assertEquals(10, result.hour) },
            { assertEquals(30, result.minute) }
        )
    }

    @Test
    fun `toInstant - should convert LocalDateTime to Instant`() {
        val localDateTime = LocalDateTime.of(2024, 3, 15, 10, 30)
        val result = localDateTime.toInstant()
        assertAll(
            { assertEquals(2024, result?.atZone(ZoneId.systemDefault())?.year) },
            { assertEquals(3, result?.atZone(ZoneId.systemDefault())?.monthValue) },
            { assertEquals(15, result?.atZone(ZoneId.systemDefault())?.dayOfMonth) }
        )
    }

    @Test
    fun `toInstant - should return null for null input`() {
        val localDateTime: LocalDateTime? = null
        assertNull(localDateTime.toInstant())
    }

    @Test
    fun `formatDate - should format Instant with pattern`() {
        val instant = Instant.parse("2024-03-15T10:30:00Z")
        val pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val result = instant.formatDate(pattern)
        assertEquals("15.03.2024", result)
    }

    @Test
    fun `parseDate - should parse valid date string`() {
        val result = parseDate("15.03.2024")
        assertAll(
            { assertEquals(15, result.toLocalDateTime().dayOfMonth) },
            { assertEquals(3, result.toLocalDateTime().monthValue) },
            { assertEquals(2024, result.toLocalDateTime().year) }
        )
    }

    @Test
    fun `parseDate - should throw for invalid format`() {
        val exception = assertThrows<InvalidDateException> {
            parseDate("2024-03-15")
        }
        assertEquals(BotErrors.INVALID_DATE.msg, exception.message)
    }

    @Test
    fun `parseDate - should throw for future date`() {
        val futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val exception = assertThrows<InvalidDateException> {
            parseDate(futureDate)
        }
        assertEquals(FUTURE_DATE, exception.message)
    }

    @Test
    fun `parseDate - should throw for date before 1970`() {
        val exception = assertThrows<InvalidDateException> {
            parseDate("31.12.1969")
        }
        assertEquals(NOT_UNIX_DATE, exception.message)
    }
}