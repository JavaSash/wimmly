package ru.telegram.bot.adapter.strategy.dto

import ru.telegram.bot.adapter.exceptions.InvalidDateException
import java.time.format.DateTimeParseException

data class ErrorDto(
    val errorMsg: String
): DataModel

enum class BotErrors(val msg: String) {
    INVALID_DATE("Дата должна быть в формате ДД.ММ.ГГГГ"),

    INVALID_AMOUNT("Неправильная сумма"),// todo

    UNKNOWN("Пу-пу-пу. Вы что-то нажали и всё сломалось.\nВызовите /balance и попробуйте снова")
}

fun formErrorMsg(exc: Throwable) = when (exc) {
    is InvalidDateException -> exc.message!!
    is DateTimeParseException -> BotErrors.INVALID_DATE.msg
    else -> BotErrors.UNKNOWN.msg
}