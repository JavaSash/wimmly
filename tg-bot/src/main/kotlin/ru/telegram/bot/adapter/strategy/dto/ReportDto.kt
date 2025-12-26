package ru.telegram.bot.adapter.strategy.dto

import ru.telegram.bot.adapter.dto.budget.PeriodReport
import java.math.BigDecimal

data class ReportDto(
    val balance: BigDecimal,
    val income: ReportDetail,
    val expense: ReportDetail,
    val periodName: String
): DataModel

data class ReportDetail(
    val amount: BigDecimal,
    /**
     * Key - category name (enum value)
     * value - sum for category
     */
    val amountByCategory: Map<String, BigDecimal> = emptyMap()
)

fun getReportStub(): ReportDto =
    ReportDto(income = formEmptyReportDetail(), expense = formEmptyReportDetail(), balance = BigDecimal.ZERO, periodName = "default")

private fun formEmptyReportDetail() = ReportDetail(amount = BigDecimal.ZERO)

fun mapToReportDto(periodReport: PeriodReport) =
    ReportDto(
        balance = periodReport.balance,
        income = ReportDetail(
            amount = periodReport.income.txTypeAmount,
            amountByCategory = periodReport.income.amountByCategory
        ),
        expense = ReportDetail(
            amount = periodReport.expense.txTypeAmount,
            amountByCategory = periodReport.expense.amountByCategory
        ),
        periodName = periodReport.periodName
    )