package ru.telegram.bot.adapter.strategy.dto

data class ButtonResponseDto(
    val chatId: Long, val text: String?, val accept: String?
): DataModel