package ru.telegram.bot.adapter.strategy.data.report

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.client.ReportClient
import ru.telegram.bot.adapter.strategy.data.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.BalanceDto
import java.math.BigDecimal

/**
 * Data provider for balance
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 * T should extend from DataModel
 */
@Repository
class BalanceRepository(
    private val reportClient: ReportClient
) : AbstractRepository<BalanceDto>() {
    companion object : KLogging()

    /**
     * @return balance from budget-service or stub
     */
    override fun getData(chatId: Long): BalanceDto {
        logger.info { "$$$ Try to get balance for chat: $chatId" }
        return runCatching {
            val report = reportClient.getThisMonthReport(chatId.toString())
            BalanceDto(balance = report.balance, income = report.totalIncome, expense = report.totalExpense)
        }
            .onFailure { logger.error { "$$$ Can't receive balance for user: $chatId. Cause: ${it.message}. Return stub" } }
            .getOrDefault(getBalanceStub())
    }

    private fun getBalanceStub(): BalanceDto =
        BalanceDto(income = BigDecimal.ZERO, expense = BigDecimal.ZERO, balance = BigDecimal.ZERO)
}