package ru.telegram.bot.adapter.strategy.stepper.common

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository

@Component
class FinalStep(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
    private val searchContextRepository: SearchContextRepository
) : Step {

    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? {
        logger.info { "$$$ FinalStep.cleanup for chat: $chatId" }

        runCatching {
            chatContextRepository.clearDialogState(chatId)
            transactionDraftRepository.clearDialogState(chatId)
            searchContextRepository.clearDialogState(chatId)
            logger.info { "$$$ Dialog cleanup successful for chat: $chatId" }
        }.onFailure { e ->
            logger.error(e) { "$$$ Failed to cleanup dialog for chat: $chatId" }
        }
        return null // no steps after
    }
}