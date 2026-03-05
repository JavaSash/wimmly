package ru.telegram.bot.adapter.strategy.logic.transaction

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class EnterCommentChooser(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
) : MessageChooser {
    override fun execute(chatId: Long, message: Message) {
        transactionDraftRepository.updateComment(chatId, message.text)
        // todo del, clear in FINAL step
        chatContextRepository.updateAccept(chatId, false) // set to default to use in another buttons
        chatContextRepository.updateUserStep(chatId, StepCode.CREATE_TRANSACTION)
    }
}