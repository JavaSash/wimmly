package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.TransactionIdFormatException
import ru.telegram.bot.adapter.exceptions.TransactionIdNotExistException
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.MAX_TRX_ID

@Component
class DeleteTransactionChooser(
    private val searchContextRepository: SearchContextRepository,
    private val transactionService: TransactionService,
    private val errorService: ErrorService
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        logger.info { "$$$ DeleteTransactionChooser.execute with params: \nchatId=$chatId\nmessage=$message" }
        val trxIdString = message.text?.trim()
        runCatching {
            searchContextRepository.updateTrxId(chatId, parseTransactionId(chatId = chatId, trxIdString = trxIdString))
        }.onFailure { exc ->
            errorService.logError(
                chatId = chatId,
                exc = exc,
                data = trxIdString,
                errorStep = StepCode.DELETE_TRANSACTION
            )
        }
    }

    private fun parseTransactionId(chatId: Long, trxIdString: String?): Long {
        logger.info { "$$$ [DeleteTransactionChooser] Try to parse trxId: $trxIdString for chat: $chatId" }
        // только цифра
        val trxId = trxIdString?.toLongOrNull()
            ?: throw TransactionIdFormatException(BotErrors.TRX_ID_NOT_NUMBER.msg)
        // логические ограничения номера
        if (trxId <= 0 || trxId > MAX_TRX_ID)
            throw TransactionIdFormatException(BotErrors.TRX_ID_NOT_EXIST.msg)
        // транзакция с id не существует у пользователя
        if (!transactionService.isExist(chatId = chatId, displayId = trxId))
            throw TransactionIdNotExistException(BotErrors.TRX_ID_NOT_EXIST.msg)

        return trxId
    }
}