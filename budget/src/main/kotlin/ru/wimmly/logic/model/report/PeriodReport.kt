package ru.wimmly.logic.model.report

import ru.wimmly.logic.model.transaction.TransactionCategory
import java.math.BigDecimal

data class PeriodReport(
    val periodName: String,
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val details: List<ReportItem>
)

data class ReportItem(
    val category: TransactionCategory,
    val total: BigDecimal
)
