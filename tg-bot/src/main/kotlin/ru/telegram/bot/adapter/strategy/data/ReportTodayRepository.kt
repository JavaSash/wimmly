package ru.telegram.bot.adapter.strategy.data

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.client.ReportClient
import ru.telegram.bot.adapter.strategy.dto.BalanceDto
import java.math.BigDecimal

/**
 * Data provider for balance
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 * T should extend from DataModel
 */
@Repository
class ReportTodayRepository(
    private val reportClient: ReportClient
) : AbstractRepository<BalanceDto>() {
    companion object : KLogging()

    /**
     * @return today's report from budget-service or stub
     */
    override fun getData(chatId: Long): BalanceDto {
        logger.info { "$$$ Try to get today's report for chat: $chatId" }
        return runCatching {
            val monthReport = reportClient.getTodayReport(chatId.toString())
            BalanceDto(balance = monthReport.currentBalance, income = monthReport.totalIncome, expense = monthReport.totalExpense)
        }
            .onFailure { logger.error { "$$$ Can't receive today's report for user: $chatId. Cause: ${it.message}. Return stub" } }
            .getOrDefault(getBalanceStub())
    }

    private fun getBalanceStub(): BalanceDto =
        BalanceDto(income = BigDecimal.ZERO, expense = BigDecimal.ZERO, balance = BigDecimal.ZERO)
}