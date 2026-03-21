package ru.telegram.bot.adapter.strategy.stepper.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class CreateTransactionStep(
    private val transactionDraftRepository: TransactionDraftRepository,
    private val txService: TransactionService
) : Step {
    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? {
        logger.info { "$$$ CreateTransactionStep.execute for chanId=$chatId" }
        val trxDraft = transactionDraftRepository.getTransactionDraft(chatId)!! // todo error when draft is null
        txService.addTransaction(trxDraft) // todo runCatching + onFailure async addTx
        return StepCode.BALANCE
    }
}