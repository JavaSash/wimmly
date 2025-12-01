package ru.wimmly.logic.model.report

import java.math.BigDecimal

data class Balance(
    val income: BigDecimal,
    val expense: BigDecimal,
    val balance: BigDecimal
)
