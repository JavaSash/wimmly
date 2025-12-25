package ru.telegram.bot.adapter.strategy.stepper.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class CreateTransactionStep(
    private val usersRepository: UsersRepository,
    private val txService: TransactionService
) : Step {
    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode? {
        logger.info { "$$$ CreateTransactionChooser.execute for chanId=$chatId" }
        txService.addTransaction(usersRepository.getUser(chatId)!!) // todo runCatching + onFailure async addTx
        usersRepository.updateUserStep(chatId, StepCode.BALANCE)
        return StepCode.BALANCE
    }
}