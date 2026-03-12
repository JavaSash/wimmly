package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.parseAmount

@Component
class EnterAmountChooser(
    private val transactionDraftRepository: TransactionDraftRepository,
    private val errorService: ErrorService
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        logger.info { "$$$ EnterAmountChooser.execute with params: \nchatId=$chatId\nmessage=$message" }
        val amountText = message.text?.trim()

        runCatching {
            transactionDraftRepository.updateAmount(chatId, parseAmount(amountText))
        }.onFailure { exc ->
            errorService.logError(chatId = chatId, exc = exc, data = amountText, errorStep = StepCode.ENTER_AMOUNT)
        }
    }
}