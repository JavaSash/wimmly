package ru.telegram.bot.adapter.strategy.dto

import ru.telegram.bot.adapter.utils.Constants.Transaction.COMMENT_MAX_LENGTH
import ru.telegram.bot.adapter.utils.Constants.Transaction.MAX_AMOUNT

data class ErrorDto(
    val errorMsg: String
): DataModel

enum class BotErrors(val msg: String) {
    // Date errors
    INVALID_DATE("Дата должна быть в формате ДД.ММ.ГГГГ"),
    FUTURE_DATE("Дата не может быть в будущем"),
    NOT_UNIX_DATE("Дата должна быть после 1970 года"),
    END_BEFORE_START_DATE("Начальная дата должна быть раньше конечной"),

    // Number errors
    INVALID_AMOUNT("Сумма должна быть вида: 1; 1.21; 2,37; 2.01; 5,1; 7.40"),
    AMOUNT_SHOULD_BE_POSITIVE("Сумма должна быть больше нуля"),
    AMOUNT_SHOULD_BE_SMALLER("Сумма должна быть меньше $MAX_AMOUNT"),
    TRX_ID_NOT_NUMBER("Номер транзакции должен быть числом"),

    // String errors
    TOO_LONG_COMMENT("Комментарий должен быть короче $COMMENT_MAX_LENGTH символов"),

    // Logic errors
    TRX_ID_NOT_EXIST("Нет транзакции с таким номером"),

    UNKNOWN("Пу-пу-пу. Вы что-то нажали и всё сломалось.\nВызовите /balance и попробуйте снова")
}