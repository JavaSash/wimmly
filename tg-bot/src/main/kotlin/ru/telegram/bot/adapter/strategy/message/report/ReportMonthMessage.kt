package ru.telegram.bot.adapter.strategy.message.report

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.service.ReportService
import ru.telegram.bot.adapter.strategy.dto.ReportDto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import ru.telegram.bot.adapter.utils.formatMoney
import java.math.BigDecimal

@Component
class ReportMonthMessage(
    messageWriter: MessageWriter,
    private val reportService: ReportService
) : AbstractSendMessage<ReportDto>(messageWriter) {

    companion object : KLogging()

    /**
     * @param data - income msg from MessageContext.getMessage()
     * form user's balance data and add it to ftl template
     * @return string data of .ftl file
     */
    override fun message(data: ReportDto?): String {
        logger.info { "$$$ ReportMonthMessage.message data: $data" }

        val incomeCategories = reportService.prepareCategories(
            data?.income?.amountByCategory ?: emptyMap(),
            data?.income?.amount ?: BigDecimal.ZERO
        )

        val expenseCategories = reportService.prepareCategories(
            data?.expense?.amountByCategory ?: emptyMap(),
            data?.expense?.amount ?: BigDecimal.ZERO,
            isExpense = true
        )

        val templateData = mapOf(
            "balance" to data?.balance?.formatMoney(),
            "income" to data?.income?.amount?.formatMoney(),
            "expense" to data?.expense?.amount?.formatMoney(),
            "hasIncomeCategories" to (data?.income?.amountByCategory?.isNotEmpty() == true),
            "incomeCategories" to incomeCategories,
            "hasExpenseCategories" to (data?.expense?.amountByCategory?.isNotEmpty() == true),
            "expenseCategories" to expenseCategories
        )
        return messageWriter.process(StepCode.REPORT_MONTH, templateData)
    }
}