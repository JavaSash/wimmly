package ru.telegram.bot.adapter.strategy.logic.transaction

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class EnterCommentChooser(
    private val transactionDraftRepository: TransactionDraftRepository,
) : MessageChooser {
    override fun execute(chatId: Long, message: Message) {
        transactionDraftRepository.updateComment(chatId, message.text)
    }
}