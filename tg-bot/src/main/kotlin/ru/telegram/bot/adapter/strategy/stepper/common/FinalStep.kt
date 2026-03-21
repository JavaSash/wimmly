package ru.telegram.bot.adapter.strategy.stepper.common

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository

/**
 * Step to clean up temporary dialogue tables
 */
@Component
class FinalStep(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
    private val searchContextRepository: SearchContextRepository
) : Step {

    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? {
        logger.info { "$$$ FinalStep.cleanup for chat: $chatId" }

        var hasErrors = false

        runCatching { chatContextRepository.clearDialogState(chatId) }
            .onFailure {
                logger.error { "$$$ Failed to cleanup chat context for chat: $chatId" }
                hasErrors = true
            }

        runCatching { transactionDraftRepository.clearDialogState(chatId) }
            .onFailure {
                logger.error { "$$$ Failed to cleanup transaction draft for chat: $chatId" }
                hasErrors = true
            }

        runCatching { searchContextRepository.clearDialogState(chatId) }
            .onFailure {
                logger.error { "$$$ Failed to cleanup search context for chat: $chatId" }
                hasErrors = true
            }

        if (!hasErrors) {
            logger.info { "$$$ Dialog cleanup successful for chat: $chatId" }
        }
        return null // no steps after
    }
}