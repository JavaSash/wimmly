package ru.telegram.bot.adapter.strategy.logic.common

import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

interface Chooser {

    fun classStepCode(): StepCode = this.currentStepCode("Chooser")

}
