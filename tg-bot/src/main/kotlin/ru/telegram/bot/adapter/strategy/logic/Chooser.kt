package ru.telegram.bot.adapter.strategy.logic

import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

interface Chooser {

    fun classStepCode() = this.currentStepCode("Chooser")

}
