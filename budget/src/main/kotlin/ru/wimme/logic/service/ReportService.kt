package ru.wimme.logic.service

import mu.KLogging
import org.springframework.stereotype.Service
import ru.wimme.logic.model.report.Period
import ru.wimme.logic.model.report.PeriodReport
import ru.wimme.logic.model.report.TxTypeDetail
import ru.wimme.logic.model.transaction.TransactionCategory
import ru.wimme.logic.model.transaction.TransactionType
import ru.wimme.logic.utils.Constants.Date.ZONE_OFFSET
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*

@Service
class ReportService(
    private val txService: TransactionService,
    private val balanceService: BalanceService
) {
    companion object : KLogging() {
        const val TODAY = "Сегодня"
        const val WEEK = "Неделя"
        const val MONTH = "Месяц"
        const val YEAR = "Год"
    }

    fun formTodayReport(userId: String): PeriodReport {
        val today = LocalDate.now(ZONE_OFFSET)
        val from = today.atStartOfDay(ZONE_OFFSET).toInstant()
        val to = today.plusDays(1).atStartOfDay(ZONE_OFFSET).toInstant()

        return reportForPeriod(
            userId = userId,
            from = from,
            to = to,
            label = TODAY // todo date
        )
    }

    fun formThisWeekReport(userId: String): PeriodReport {
        val week = WeekFields.of(Locale.getDefault())
        val now = LocalDate.now(ZONE_OFFSET)
        val start = now.with(week.dayOfWeek(), 1).atStartOfDay(ZONE_OFFSET).toInstant()
        val end = start.plus(Duration.ofDays(7))
        return reportForPeriod(userId, start, end, WEEK) // todo period
    }

    fun formThisMonthReport(userId: String): PeriodReport {
        val now = LocalDate.now(ZONE_OFFSET)
        val start = now.withDayOfMonth(1).atStartOfDay(ZONE_OFFSET).toInstant()
        val end = start.plus(Duration.ofDays(now.lengthOfMonth().toLong()))
        return reportForPeriod(userId, start, end, MONTH) // todo period
    }

    fun formThisYearReport(userId: String): PeriodReport {
        val now = LocalDate.now(ZONE_OFFSET)
        val start = now.withDayOfYear(1).atStartOfDay(ZONE_OFFSET).toInstant()
        val end = start.plus(Duration.ofDays(now.lengthOfYear().toLong()))
        return reportForPeriod(userId, start, end, YEAR) // todo current year
    }

    fun reportForPeriod(
        userId: String,
        from: Instant,
        to: Instant,
        label: String
    ): PeriodReport {
        val transactions = txService.getUserTransactions(userId = userId, from = from, to = to)
        logger.info { "$$$ ${transactions.size} found for period $from - $to" }

        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO

        val incomeByCategory = mutableMapOf<TransactionCategory, BigDecimal>()
        val expenseByCategory = mutableMapOf<TransactionCategory, BigDecimal>()

        transactions.forEach { tx ->
            when (tx.type) {
                TransactionType.INCOME -> {
                    totalIncome = totalIncome.add(tx.amount)
                    val category = TransactionCategory.fromCode(tx.category, TransactionType.INCOME)
                    incomeByCategory[category] = incomeByCategory.getOrDefault(category, BigDecimal.ZERO).add(tx.amount)
                }

                TransactionType.EXPENSE -> {
                    totalExpense = totalExpense.add(tx.amount)
                    val category = TransactionCategory.fromCode(tx.category, TransactionType.EXPENSE)
                    expenseByCategory[category] =
                        expenseByCategory.getOrDefault(category, BigDecimal.ZERO).add(tx.amount)
                }
            }
        }

        return PeriodReport(
            balance = balanceService.getBalance(
                userId = userId,
                period = Period(from = from, to = to)
            ).balance,
            periodName = label,
            income = TxTypeDetail(
                txTypeAmount = totalIncome,
                amountByCategory = incomeByCategory
            ),
            expense = TxTypeDetail(
                txTypeAmount = totalExpense,
                amountByCategory = expenseByCategory
            )
        ).also {
            logger.info { "$$$ For period ${it.periodName} report formed: $it" }
        }
    }
}