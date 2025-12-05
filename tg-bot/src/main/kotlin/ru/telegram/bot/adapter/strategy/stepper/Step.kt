package ru.telegram.bot.adapter.strategy.stepper

import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

interface Step {

    fun isAvailableForCurrentStep(stepCode: StepCode): Boolean {
        return this.currentStepCode( "Step") == stepCode
    }

    fun getNextStep(chatId: Long): StepCode?
}
