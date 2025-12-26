package ru.telegram.bot.adapter.strategy.data.report

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.client.ReportClient
import ru.telegram.bot.adapter.strategy.data.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.ReportDto
import ru.telegram.bot.adapter.strategy.dto.getReportStub
import ru.telegram.bot.adapter.strategy.dto.mapToReportDto

/**
 * Data provider for balance
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 * T should extend from DataModel
 */
@Repository
class ReportTodayRepository(
    private val reportClient: ReportClient
) : AbstractRepository<ReportDto>() {
    companion object : KLogging()

    /**
     * @return report from budget-service or stub
     */
    override fun getData(chatId: Long): ReportDto {
        logger.info { "$$$ Try to get today's report for chat: $chatId" }
        return runCatching { mapToReportDto(reportClient.getTodayReport(chatId.toString())
            .also { logger.info { "$$$ Received report for today: $it" } }
        ) }
            .onFailure { logger.error { "$$$ Can't receive today's report for user: $chatId. Cause: ${it.message}. Return stub" } }
            .getOrDefault(getReportStub())
    }
}