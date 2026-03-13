package ru.wimme.logic.service

import mu.KLogging
import org.springframework.stereotype.Service
import ru.wimme.logic.model.report.Balance
import ru.wimme.logic.model.report.Period
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class BalanceService(
    private val txService: TransactionService
) {

    companion object : KLogging()

    /**
     * If @param [period] is null
     * @return [Balance.balance] for all time
     * [Balance.income] and [Balance.expense] for current month
     *
     * If @param [period] is not null
     * @return [Balance.balance], [Balance.income] and [Balance.expense] for this period
     */
    fun getBalance(userId: String, period: Period? = null): Balance {
        return if (period == null) {
            val monthStart = LocalDate
                .now()
                .withDayOfMonth(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
            logger.info { "$$$ Form balance for user: $userId from: $monthStart to now" }
            txService.getBalance(userId = userId, periodStart = monthStart)
        } else {
            logger.info { "$$$ Form balance for user: $userId from: ${period.from} to ${period.to}" }
            txService.getBalanceForPeriod(userId = userId, from = period.from, to = period.to)
        }
    }
}