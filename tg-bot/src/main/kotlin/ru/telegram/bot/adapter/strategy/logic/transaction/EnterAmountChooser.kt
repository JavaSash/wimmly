package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class EnterAmountChooser(
    private val transactionDraftRepository: TransactionDraftRepository,
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        logger.info { "$$$ EnterAmountChooser.execute with params: \nchanId=$chatId\nmessage=$message" }
        val amountText = message.text?.trim()

        if (isValidAmount(amountText)) {
            transactionDraftRepository.updateAmount(chatId, amountText!!)
        } // todo else error step
    }

    private fun isValidAmount(amount: String?): Boolean {
        return amount?.matches(Regex("\\d+(\\.\\d{1,2})?")) ?: false
    }
}