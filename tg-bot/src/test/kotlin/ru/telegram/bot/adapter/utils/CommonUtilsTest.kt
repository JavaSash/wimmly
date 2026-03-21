package ru.telegram.bot.adapter.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

class CommonUtilsTest {

    /**
     * Test on classes with default constructor without params
     */
    @ParameterizedTest
    @ValueSource(
        strings = [
            "AddExpenseStep",
            "AskTransactionTypeStep",
            "SearchTransactionsStep",
        ]
    )
    fun `currentStepCode should convert class name to correct StepCode`(className: String) {
        val clazz = Class.forName("ru.telegram.bot.adapter.strategy.stepper.transaction.$className")
        val step = clazz.getDeclaredConstructor().newInstance()

        val result = step.currentStepCode("Step")

        val expectedStepCodeName = className
            .removeSuffix("Step")
            .replace(CommonUtils.REGEXP, "_$0")
            .uppercase()

        assertEquals(StepCode.valueOf(expectedStepCodeName), result)
    }

    @Test
    fun `currentStepCode should throw IllegalArgumentException when StepCode not found`() {
        val step = object {}.javaClass

        assertThrows<IllegalArgumentException> { step.currentStepCode("Step") }
    }

    @Test
    fun `currentStepCode should convert to uppercase correctly`() {
        val clazz = Class.forName("ru.telegram.bot.adapter.strategy.stepper.transaction.AddExpenseStep")
        val step = clazz.getDeclaredConstructor().newInstance()

        val result = step.currentStepCode("Step")

        assertEquals(StepCode.ADD_EXPENSE, result)
    }
}