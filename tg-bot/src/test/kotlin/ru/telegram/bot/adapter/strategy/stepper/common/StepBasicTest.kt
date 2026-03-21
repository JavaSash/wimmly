package ru.telegram.bot.adapter.strategy.stepper.common

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode

/**
 * To check nextStep for Step class extend from [StepBasicTest]
 * and init in @BeforeEach [step] testing Step instance and [nextStep]
 */
@ExtendWith(MockitoExtension::class)
abstract class StepBasicTest {

    protected lateinit var step: Step
    protected lateinit var nextStep: StepCode

    @Test
    fun `getNextStep should return next step code`() {
        Assertions.assertEquals(nextStep, step.getNextStep(CHAT_ID))
    }
}