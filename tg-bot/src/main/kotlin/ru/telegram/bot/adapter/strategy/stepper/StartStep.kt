package ru.telegram.bot.adapter.strategy.stepper

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode

@Component
class StartStep : Step {

    override fun getNextStep(chatId: Long): StepCode {
        return StepCode.USER_INFO
    }

}