package ru.telegram.bot.adapter.strategy.dto

data class ErrorDto(
    val errorMsg: String
): DataModel

enum class BotErrors(val msg: String) {
    INVALID_DATE("Дата должна быть в формате ДД.ММ.ГГГГ"),
    INVALID_AMOUNT("Сумма должна быть вида: 1; 1.21; 2,37; 2.01; 5,1; 7.40"),
    UNKNOWN("Пу-пу-пу. Вы что-то нажали и всё сломалось.\nВызовите /balance и попробуйте снова")
}