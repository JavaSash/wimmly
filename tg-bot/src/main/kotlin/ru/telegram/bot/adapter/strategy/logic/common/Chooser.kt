package ru.telegram.bot.adapter.strategy.logic.common

import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

interface Chooser {

    fun classStepCode() = this.currentStepCode("Chooser")

}
