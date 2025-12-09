package ru.telegram.bot.adapter.strategy.stepper.common

import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

/**
 * Implement it for new step
 */
interface Step {

    fun isAvailableForCurrentStep(stepCode: StepCode): Boolean {
        return this.currentStepCode( "Step") == stepCode
    }

    /**
     * Which step is next
     */
    fun getNextStep(chatId: Long): StepCode?
}
