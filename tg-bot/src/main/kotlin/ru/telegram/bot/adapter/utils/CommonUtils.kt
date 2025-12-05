package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.dto.enums.StepCode

object CommonUtils {

    val REGEXP = "(?<=.)[A-Z]".toRegex()

    fun Any.currentStepCode(removeSuffix: String): StepCode {
        val stepCodeName = this
            .javaClass
            .simpleName
            .removeSuffix(removeSuffix)
            .replace(REGEXP, "_$0")
            .uppercase()

        return StepCode.valueOf(stepCodeName)
    }

}