package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.exceptions.InvalidDateException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.utils.Constants.Date.ZONE_OFFSET
import ru.telegram.bot.adapter.utils.Constants.Transaction.FLEXIBLE_DATE_FORMAT
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun validateDate(date: LocalDateTime) {
    if (date.isAfter(LocalDateTime.now())) {
        throw InvalidDateException(BotErrors.FUTURE_DATE.msg)
    }

    if (date.isBefore(LocalDateTime.of(1970, 1, 1, 0, 0))) {
        throw InvalidDateException(BotErrors.NOT_UNIX_DATE.msg)
    }
}

fun validateDate(date: Instant) {
    val ldt = date.toLocalDateTime()

    if (ldt.isAfter(LocalDateTime.now())) {
        throw InvalidDateException(BotErrors.FUTURE_DATE.msg)
    }

    if (ldt.isBefore(LocalDateTime.of(1970, 1, 1, 0, 0))) {
        throw InvalidDateException(BotErrors.NOT_UNIX_DATE.msg)
    }
}

fun validateDate(date: ZonedDateTime) {
    if (date.isAfter(ZonedDateTime.now())) {
        throw InvalidDateException(BotErrors.FUTURE_DATE.msg)
    }

    if (date.isBefore(ZonedDateTime.of(LocalDateTime.of(1970, 1, 1, 0, 0), ZONE_OFFSET))) {
        throw InvalidDateException(BotErrors.NOT_UNIX_DATE.msg)
    }
}

fun validatePeriod(from: LocalDateTime, to: LocalDateTime) {
    validateDate(from)
    validateDate(to)
    if (to.isBefore(from)) {
        throw InvalidDateException(BotErrors.END_BEFORE_START_DATE.msg)
    }
}

fun validatePeriod(from: ZonedDateTime, to: ZonedDateTime) {
    validateDate(from)
    validateDate(to)
    if (to.isBefore(from)) {
        throw InvalidDateException(BotErrors.END_BEFORE_START_DATE.msg)
    }
}

fun Instant.toLocalDateTime(): LocalDateTime =
    this.atZone(ZONE_OFFSET).toLocalDateTime()

fun LocalDateTime?.toInstant(): Instant? =
    this?.atZone(ZONE_OFFSET)?.toInstant()

fun Instant.formatDate(pattern: DateTimeFormatter): String =
    this.atZone(ZONE_OFFSET).format(pattern)

/**
 * @return parsed date (Instant), throws [InvalidDateException]
 * Need handle it and use [ru.telegram.bot.adapter.service.ErrorService.logError] to process error handle flow
 */
fun parseDate(dateString: String): Instant {
    val date: Instant = runCatching {
        LocalDate.parse(dateString, FLEXIBLE_DATE_FORMAT).atStartOfDay(ZONE_OFFSET).toInstant()
    }.getOrNull() ?: throw InvalidDateException(BotErrors.INVALID_DATE.msg)

    validateDate(date)

    return date
}