package ru.telegram.bot.adapter.strategy.dto

data class PhotoButtonDto(
    val chatId: Long,
    val url: String,
    val buttons: List<String>
): DataModel