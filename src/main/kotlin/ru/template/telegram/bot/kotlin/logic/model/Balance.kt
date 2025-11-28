package ru.template.telegram.bot.kotlin.logic.model

import java.math.BigDecimal

data class Balance(
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal
)
