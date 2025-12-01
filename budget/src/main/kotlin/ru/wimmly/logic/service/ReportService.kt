package ru.wimmly.logic.service

import org.springframework.stereotype.Service
import ru.wimmly.logic.model.report.PeriodReport
import ru.wimmly.logic.model.report.ReportItem
import ru.wimmly.logic.model.transaction.TransactionCategory
import ru.wimmly.logic.model.transaction.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*

@Service
class ReportService(
    private val txService: TransactionService
) {

    fun formTodayReport(userId: String): PeriodReport =
        reportForPeriod(
            userId = userId,
            from = LocalDate.now().atStartOfDay(),
            to = LocalDate.now().plusDays(1).atStartOfDay(),
            label = "Сегодня" // todo date
        )

    fun formThisWeekReport(userId: String): PeriodReport {
        val week = WeekFields.of(Locale.getDefault())
        val now = LocalDate.now()
        val start = now.with(week.dayOfWeek(), 1).atStartOfDay()
        val end = start.plusWeeks(1)
        return reportForPeriod(userId, start, end, "Эта неделя") // todo period
    }

    fun formThisMonthReport(userId: String): PeriodReport {
        val now = LocalDate.now()
        val start = now.withDayOfMonth(1).atStartOfDay()
        val end = start.plusMonths(1)
        return reportForPeriod(userId, start, end, "Этот месяц") // todo period
    }

    fun formThisYearReport(userId: String): PeriodReport {
        val now = LocalDate.now()
        val start = now.withDayOfYear(1).atStartOfDay()
        val end = start.plusYears(1)
        return reportForPeriod(userId, start, end, "Этот год") // todo current year
    }

    fun reportForPeriod(
        userId: String,
        from: LocalDateTime,
        to: LocalDateTime,
        label: String
    ): PeriodReport {

        val transactions = txService.getUserTransactions(userId)
            .filter { it.createdAt!!.isAfter(from) && it.createdAt.isBefore(to) } // todo move to db lvl

        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO

        val groupedByCategory = mutableMapOf<Enum<*>, BigDecimal>()

        transactions.forEach { tx ->
            when (tx.type) {
                TransactionType.INCOME -> totalIncome = totalIncome.add(tx.amount)
                TransactionType.EXPENSE -> totalExpense = totalExpense.add(tx.amount)
            }

            groupedByCategory[tx.category] =
                groupedByCategory.getOrDefault(tx.category, BigDecimal.ZERO)
                    .add(tx.amount)
        }

        val sumByCategories = groupedByCategory.map { (cat, sum) ->
            ReportItem(
                category = cat as TransactionCategory,
                total = sum
            )
        }.sortedByDescending { it.total }

        return PeriodReport(
            periodName = label,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            details = sumByCategories
        )
    }
}