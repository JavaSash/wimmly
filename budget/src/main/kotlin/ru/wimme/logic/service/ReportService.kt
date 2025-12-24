package ru.wimme.logic.service

import org.springframework.stereotype.Service
import ru.wimme.logic.model.report.Period
import ru.wimme.logic.model.report.PeriodReport
import ru.wimme.logic.model.report.ReportItem
import ru.wimme.logic.model.transaction.TransactionCategory
import ru.wimme.logic.model.transaction.TransactionType
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.*

@Service
class ReportService(
    private val txService: TransactionService,
    private val balanceService: BalanceService
) {

    fun formTodayReport(userId: String): PeriodReport =
        reportForPeriod(
            userId = userId,
            from = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant(),
            to = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
            label = "Сегодня" // todo date
        )

    fun formThisWeekReport(userId: String): PeriodReport {
        val week = WeekFields.of(Locale.getDefault())
        val now = LocalDate.now()
        val start = now.with(week.dayOfWeek(), 1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = start.plus(Duration.ofDays(7))
        return reportForPeriod(userId, start, end, "Эта неделя") // todo period
    }

    fun formThisMonthReport(userId: String): PeriodReport {
        val now = LocalDate.now()
        val start = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = start.plus(Duration.ofDays(now.lengthOfMonth().toLong()))
        return reportForPeriod(userId, start, end, "Этот месяц") // todo period
    }

    fun formThisYearReport(userId: String): PeriodReport {
        val now = LocalDate.now()
        val start = now.withDayOfYear(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = start.plus(Duration.ofDays(now.lengthOfYear().toLong()))
        return reportForPeriod(userId, start, end, "Этот год") // todo current year
    }

    fun reportForPeriod(
        userId: String,
        from: Instant,
        to: Instant,
        label: String
    ): PeriodReport {

        val transactions = txService.getUserTransactions(userId)
            .filter { it.createdAt!!.isAfter(from) && it.createdAt.isBefore(to) } // todo move to db lvl

        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO

        val groupedByCategory = mutableMapOf<TransactionCategory, BigDecimal>()

        transactions.forEach { tx ->
            when (tx.type) {
                TransactionType.INCOME -> totalIncome = totalIncome.add(tx.amount)
                TransactionType.EXPENSE -> totalExpense = totalExpense.add(tx.amount)
            }
            val category = TransactionCategory.fromCode(tx.category, tx.type)
            groupedByCategory[category] =
                groupedByCategory.getOrDefault(category, BigDecimal.ZERO)
                    .add(tx.amount)
        }

        val sumByCategories = groupedByCategory.map { (category, sum) ->
            ReportItem(
                category = category,
                total = sum
            )
        }.sortedByDescending { it.total }

        return PeriodReport(
            balance = balanceService.getBalance(userId=userId, period = Period(from = from, to = to)).balance,
            periodName = label,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            details = sumByCategories
        )
    }
}