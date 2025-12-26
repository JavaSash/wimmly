package ru.telegram.bot.adapter.strategy.data.report

import mu.KLogging
import ru.telegram.bot.adapter.dto.budget.PeriodReport
import ru.telegram.bot.adapter.strategy.data.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.ReportDto
import ru.telegram.bot.adapter.strategy.dto.getReportStub
import ru.telegram.bot.adapter.strategy.dto.mapToReportDto

/**
 * Data provider for period report
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 */
abstract class ReportRepository: AbstractRepository<ReportDto>() {

    companion object : KLogging()

    /**
     * @return report from budget-service or stub
     */
    override fun getData(chatId: Long): ReportDto {
        logger.info { "$$$ Try to get report for chat: $chatId" }
        return runCatching { mapToReportDto(getPeriodReport(chatId.toString())
            .also { logger.info { "$$$ Received report: $it" } }
        ) }
            .onFailure { error { "$$$ Can't receive report for user: $chatId. Cause: ${it.message}. Return stub" } }
            .getOrDefault(getReportStub())
    }

    /**
     * Impl method to get data from budget-service for needed period
     */
    abstract fun getPeriodReport(chatId: String): PeriodReport
}