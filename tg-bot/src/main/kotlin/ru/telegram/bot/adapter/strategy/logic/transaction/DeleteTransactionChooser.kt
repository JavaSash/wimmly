package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class DeleteTransactionChooser(
    private val searchContextRepository: SearchContextRepository,
    private val transactionService: TransactionService
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        logger.info { "$$$ DeleteTransactionChooser.execute with params: \nchatId=$chatId\nmessage=$message" }
        runCatching {
            val trxId = message.text?.trim()?.toLong()
            if (isValidTransactionId(chatId = chatId, trxId = trxId)) {
                searchContextRepository.updateTrxId(chatId, trxId!!)
            } // todo else error step
        }.onFailure {
            logger.error { "$$$ Error in DeleteTransactionChooser.execute with cause: ${it.cause}" }
            // todo error step
        }

    }

    private fun isValidTransactionId(chatId: Long, trxId: Long?): Boolean =
        trxId?.let { transactionService.isExist(chatId = chatId, displayId = trxId) } ?: false
}