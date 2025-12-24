package ru.telegram.bot.adapter.dto.budget

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class PeriodReport(
    val currentBalance: BigDecimal,
    val periodName: String,
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal
)
