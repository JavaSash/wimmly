package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.exceptions.MaxLengthExceededException

fun String.validateStringLength(maxLength: Int, errorMsg: String) {
    if (this.length > maxLength) throw MaxLengthExceededException(errorMsg)
}