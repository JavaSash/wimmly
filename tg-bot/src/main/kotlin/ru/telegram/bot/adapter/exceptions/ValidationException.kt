package ru.telegram.bot.adapter.exceptions

open class ValidationException(msg: String): RuntimeException(msg)
class InvalidDateException(msg: String): ValidationException(msg)
class InvalidAmountException(msg: String): ValidationException(msg)
class MaxLengthExceededException(msg: String): ValidationException(msg)
class TransactionIdNotExistException(msg: String): ValidationException(msg)
class TransactionIdFormatException(msg: String): ValidationException(msg)