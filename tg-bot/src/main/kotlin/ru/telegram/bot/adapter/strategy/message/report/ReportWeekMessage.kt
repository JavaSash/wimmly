package ru.telegram.bot.adapter.strategy.message.report

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.service.ReportService

@Component
class ReportWeekMessage(
    messageWriter: MessageWriter,
    reportService: ReportService
) : ReportMessage(messageWriter, reportService)