package ru.template.telegram.bot.kotlin.logic.model.report

import ru.template.telegram.bot.kotlin.logic.model.transaction.TransactionCategory
import java.math.BigDecimal

data class PeriodReport(
    val periodName: String,
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val breakdown: List<ReportItem>
)

data class ReportItem(
    val category: TransactionCategory,
    val total: BigDecimal
)
