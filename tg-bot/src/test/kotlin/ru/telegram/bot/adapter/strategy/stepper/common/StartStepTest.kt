package ru.telegram.bot.adapter.strategy.stepper.common

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.telegram.bot.adapter.dto.enums.StepCode

/**
 * Test for [StartStep], [Step]
 */
class StartStepTest: StepBasicTest() {

    @BeforeEach
    fun setup() {
        step = StartStep()
        nextStep = StepCode.HELP
    }

    @Test
    fun `isAvailableForCurrentStep should return false for other step codes`() {
        val stepCodes = listOf(
            StepCode.SEARCH_TRANSACTIONS,
            StepCode.REPORT_TODAY,
            StepCode.ERROR
        )

        stepCodes.forEach { stepCode ->
            assertFalse(step.isAvailableForCurrentStep(stepCode))
        }
    }
}