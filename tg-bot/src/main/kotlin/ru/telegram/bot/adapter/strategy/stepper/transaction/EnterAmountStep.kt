package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class EnterAmountStep(
    private val chatContextRepository: ChatContextRepository
) : Step {

    override fun getNextStep(chatId: Long): StepCode? {
        val chatCtx = chatContextRepository.getUser(chatId)
        return if (chatCtx?.errorMsg == null && chatCtx?.errorStep == null) StepCode.ASK_DATE
        else StepCode.ERROR
    }
}