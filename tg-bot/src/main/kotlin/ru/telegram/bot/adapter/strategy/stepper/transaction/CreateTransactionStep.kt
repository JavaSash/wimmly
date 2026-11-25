package ru.telegram.bot.adapter.strategy.stepper.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.message.transaction.CreateTransactionMessage
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class CreateTransactionStep(
    private val transactionDraftRepository: TransactionDraftRepository,
    private val txService: TransactionService,
    private val createTransactionMessage: CreateTransactionMessage,
    private val telegramClient: TelegramClient,
) : Step {
    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? {
        logger.info { "$$$ CreateTransactionStep.execute for chanId=$chatId" }
        val trxDraft = transactionDraftRepository.getTransactionDraft(chatId)!! // todo error when draft is null
        val transaction = txService.addTransaction(trxDraft)
        runCatching {
            telegramClient.execute(
                SendMessage.builder()
                    .chatId(chatId)
                    .text(createTransactionMessage.render(transaction))
                    .parseMode("HTML")
                    .replyMarkup(ReplyKeyboardRemove(true))
                    .build()
            )
        }.onFailure { logger.warn(it) { "Could not send transaction confirmation for chat $chatId" } }
        return StepCode.BALANCE
    }
}
