package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.FLEXIBLE_DATE_FORMAT
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class EnterDateChooser(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
//    private val searchContextRepository: SearchContextRepository,
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        val dateText = message.text?.trim() ?: ""

        runCatching {
            // todo код для интервала дат
//            if (dateText.contains("-") && INTERVAL_PATTERN.matches(dateText)) {
//                val (fromStr, toStr) = dateText.split("-").map { it.trim() }
//                val from = LocalDate.parse(fromStr, DATE_PATTERN).atStartOfDay(ZoneOffset.UTC)
//                val to = LocalDate.parse(toStr, DATE_PATTERN).atStartOfDay(ZoneOffset.UTC)
//
//                validatePeriod(from, to)
//
//                if (to.isBefore(from)) {
//                    throw IllegalArgumentException("Конечная дата раньше начальной")
//                }
//
//                // Сохраняем интервал
//                searchContextRepository.updateDateRange(chatId, from.toInstant(), to.toInstant())
//            } else {
            val date = LocalDate.parse(dateText, FLEXIBLE_DATE_FORMAT)
            transactionDraftRepository.updateTransactionDate(chatId, date.atStartOfDay(ZoneOffset.UTC).toInstant())
            chatContextRepository.updateAccept(chatId, false) // todo del .set to default to use in another buttons
//            }
        }.onFailure { error ->
            logger.error { "$$$ EnterDateChooser.execute incorrect date format: $dateText " }
            // При ошибке остаемся на том же шаге и показываем сообщение
            chatContextRepository.updateText(chatId, "Ошибка: ${error.message}\n\nПопробуйте снова:")
            // Остаемся на StepCode.ENTER_DATE для повторного ввода
        }
    }
}