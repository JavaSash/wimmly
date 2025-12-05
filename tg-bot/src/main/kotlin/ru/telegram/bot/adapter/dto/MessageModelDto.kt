package ru.telegram.bot.adapter.dto

import java.io.ByteArrayInputStream

data class MessageModelDto(
    /** message or caption */
    val message: String,
    /** buttons */
    val inlineButtons: List<MarkupDataDto>,
    /** steam files or photo */
    val file: ByteArrayInputStream? = null,
    /** replyButtons */
    val replyButtons: List<ReplyMarkupDto> = emptyList()
)
