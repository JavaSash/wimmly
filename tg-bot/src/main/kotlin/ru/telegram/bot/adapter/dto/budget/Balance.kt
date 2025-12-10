package ru.telegram.bot.adapter.dto.budget

import java.math.BigDecimal

data class Balance(
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal
)
