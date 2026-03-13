package ru.wimme.logic.model.report

import java.math.BigDecimal

data class Balance(
    val balance: BigDecimal,
    val income: BigDecimal,
    val expense: BigDecimal
)
