package ru.telegram.bot.adapter.strategy.data.report

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.client.ReportClient
import ru.telegram.bot.adapter.dto.budget.PeriodReport

@Repository
class ReportTodayRepository(
    private val reportClient: ReportClient
) : ReportRepository() {

    override fun getPeriodReport(chatId: String): PeriodReport = reportClient.getTodayReport(chatId)
}