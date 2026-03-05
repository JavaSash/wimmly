package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class SelectCategoryStep(private val chatContextRepository: ChatContextRepository) : Step {

    override fun getNextStep(chatId: Long): StepCode? {
        return when (chatContextRepository.getUser(chatId)?.flowContext) {
            StepCode.ADD_INCOME.name, StepCode.ADD_EXPENSE.name -> StepCode.ENTER_AMOUNT // add transaction flow
            StepCode.SEARCH_TRANSACTIONS.name -> StepCode.SHOW_TRANSACTIONS // search_transactions flow
            else -> StepCode.ENTER_AMOUNT // todo default?
        }
    }
}