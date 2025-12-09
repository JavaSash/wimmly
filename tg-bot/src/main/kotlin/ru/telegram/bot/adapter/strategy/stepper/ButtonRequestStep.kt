package ru.telegram.bot.adapter.strategy.stepper

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class ButtonRequestStep : Step {

    override fun getNextStep(chatId: Long): StepCode {
        return StepCode.BUTTON_RESPONSE
    }

}