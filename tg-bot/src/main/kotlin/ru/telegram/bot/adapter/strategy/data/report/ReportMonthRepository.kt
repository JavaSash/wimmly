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
class ReportMonthRepository(
    private val reportClient: ReportClient
) : AbstractRepository<ReportDto>() {
    companion object : KLogging()

    /**
     * @return report from budget-service or stub
     */
    override fun getData(chatId: Long): ReportDto {
        logger.info { "$$$ Try to get month report for chat: $chatId" }
        return runCatching { mapToReportDto(reportClient.getThisMonthReport(chatId.toString())) }
            .onFailure { logger.error { "$$$ Can't receive month report for user: $chatId. Cause: ${it.message}. Return stub" } }
            .getOrDefault(getReportStub())
    }
}