package ru.wimme.logic.model.report

import ru.wimme.logic.model.transaction.ExpenseCategory
import java.math.BigDecimal

data class PeriodReport(
    val periodName: String,
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val details: List<ReportItem>
)

data class ReportItem(
    val category: ExpenseCategory,
    val total: BigDecimal
)
