package ru.telegram.bot.adapter.utils

import java.time.*
import java.time.format.DateTimeFormatter

fun validateDate(date: LocalDateTime) {
    if (date.isAfter(LocalDateTime.now())) {
        throw IllegalArgumentException("Дата не может быть в будущем")
    }

    if (date.isBefore(LocalDateTime.of(1970, 1, 1, 0, 0))) {
        throw IllegalArgumentException("Дата старее 1970 года")
    }
}

fun validateDate(date: ZonedDateTime) {
    if (date.isAfter(ZonedDateTime.now())) {
        throw IllegalArgumentException("Дата не может быть в будущем")
    }

    if (date.isBefore(ZonedDateTime.of(LocalDateTime.of(1970, 1, 1, 0, 0), ZoneOffset.UTC))) {
        throw IllegalArgumentException("Дата старее 1970 года")
    }
}

fun validatePeriod(from: LocalDateTime, to: LocalDateTime) {
    validateDate(from)
    validateDate(to)
    if (to.isBefore(from)) {
        throw IllegalArgumentException("Конечная дата раньше начальной")
    }
}

fun validatePeriod(from: ZonedDateTime, to: ZonedDateTime) {
    validateDate(from)
    validateDate(to)
    if (to.isBefore(from)) {
        throw IllegalArgumentException("Конечная дата раньше начальной")
    }
}

fun Instant.toLocalDateTime(): LocalDateTime =
    this.atZone(ZoneId.systemDefault()).toLocalDateTime()

fun LocalDateTime?.toInstant(): Instant? =
    this?.atZone(ZoneId.systemDefault())?.toInstant()

fun Instant.formatDate(pattern: DateTimeFormatter): String =
    this.atZone(ZoneId.systemDefault()).format(pattern)
