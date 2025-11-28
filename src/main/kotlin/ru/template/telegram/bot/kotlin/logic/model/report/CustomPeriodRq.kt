package ru.template.telegram.bot.kotlin.logic.model.report

import java.time.LocalDateTime

data class CustomPeriodRq(
    val userId: String,
    val from: LocalDateTime,
    val to: LocalDateTime
)
