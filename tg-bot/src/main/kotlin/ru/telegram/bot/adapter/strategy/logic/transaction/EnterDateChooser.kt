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
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        val dateText = message.text?.trim() ?: ""

        runCatching {
            val date = LocalDate.parse(dateText, FLEXIBLE_DATE_FORMAT)
            transactionDraftRepository.updateTransactionDate(chatId, date.atStartOfDay(ZoneOffset.UTC).toInstant())
        }.onFailure { error ->
            logger.error { "$$$ EnterDateChooser.execute incorrect date format: $dateText " }
            // При ошибке остаемся на том же шаге и показываем сообщение
            chatContextRepository.updateText(chatId, "Ошибка: ${error.message}\n\nПопробуйте снова:")
            // Остаемся на StepCode.ENTER_DATE для повторного ввода
        }
    }
}