package ru.telegram.bot.adapter.exceptions

open class ValidationException(msg: String): RuntimeException(msg)
class InvalidDateException(msg: String): ValidationException(msg)
class InvalidAmountException(msg: String): ValidationException(msg)