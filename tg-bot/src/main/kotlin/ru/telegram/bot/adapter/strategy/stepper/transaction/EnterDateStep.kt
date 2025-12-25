package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class EnterDateStep : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        return StepCode.ASK_COMMENT
    }
}