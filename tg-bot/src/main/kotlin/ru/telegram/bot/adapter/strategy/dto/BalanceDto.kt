package ru.telegram.bot.adapter.strategy.dto

import java.math.BigDecimal

data class BalanceDto(
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal
): DataModel
