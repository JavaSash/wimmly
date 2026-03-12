package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.exceptions.InvalidDateException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.utils.Constants.Errors.END_BEFORE_START_DATE
import ru.telegram.bot.adapter.utils.Constants.Errors.FUTURE_DATE
import ru.telegram.bot.adapter.utils.Constants.Errors.NOT_UNIX_DATE
import java.time.*
import java.time.format.DateTimeFormatter

fun validateDate(date: LocalDateTime) {
    if (date.isAfter(LocalDateTime.now())) {
        throw InvalidDateException(FUTURE_DATE)
    }

    if (date.isBefore(LocalDateTime.of(1970, 1, 1, 0, 0))) {
        throw InvalidDateException(NOT_UNIX_DATE)
    }
}

fun validateDate(date: Instant) {
    val ldt = date.toLocalDateTime()

    if (ldt.isAfter(LocalDateTime.now())) {
        throw InvalidDateException(FUTURE_DATE)
    }

    if (ldt.isBefore(LocalDateTime.of(1970, 1, 1, 0, 0))) {
        throw InvalidDateException(NOT_UNIX_DATE)
    }
}

fun validateDate(date: ZonedDateTime) {
    if (date.isAfter(ZonedDateTime.now())) {
        throw InvalidDateException(FUTURE_DATE)
    }

    if (date.isBefore(ZonedDateTime.of(LocalDateTime.of(1970, 1, 1, 0, 0), ZoneOffset.UTC))) {
        throw InvalidDateException(NOT_UNIX_DATE)
    }
}

fun validatePeriod(from: LocalDateTime, to: LocalDateTime) {
    validateDate(from)
    validateDate(to)
    if (to.isBefore(from)) {
        throw InvalidDateException(END_BEFORE_START_DATE)
    }
}

fun validatePeriod(from: ZonedDateTime, to: ZonedDateTime) {
    validateDate(from)
    validateDate(to)
    if (to.isBefore(from)) {
        throw InvalidDateException(END_BEFORE_START_DATE)
    }
}

fun Instant.toLocalDateTime(): LocalDateTime =
    this.atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime?.toInstant(): Instant? =
    this?.atZone(ZoneId.systemDefault())?.toInstant()

fun Instant.formatDate(pattern: DateTimeFormatter): String =
    this.atZone(ZoneId.systemDefault()).format(pattern)

/**
 * @return parsed date (Instant), throws [InvalidDateException]
 * Need handle it and use [ru.telegram.bot.adapter.service.ErrorService.logError] to process error handle flow
 */
fun parseDate(dateString: String): Instant {
    val date: Instant = runCatching {
        LocalDate.parse(dateString, Constants.Transaction.FLEXIBLE_DATE_FORMAT).atStartOfDay(ZoneOffset.UTC).toInstant()
    }.getOrNull() ?: throw InvalidDateException(BotErrors.INVALID_DATE.msg)

    validateDate(date)

    return date
}