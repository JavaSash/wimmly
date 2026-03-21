package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class RemoveTransactionStep(
    private val searchContextRepository: SearchContextRepository,
    private val transactionService: TransactionService
) : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        transactionService.removeTransaction(chatId, searchContextRepository.findById(chatId)?.trxId!!) // todo error step for trxId null and when data not found?
        return StepCode.FINAL
    }
}