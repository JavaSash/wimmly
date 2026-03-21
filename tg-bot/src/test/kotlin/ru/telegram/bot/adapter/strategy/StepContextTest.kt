package ru.telegram.bot.adapter.strategy

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.stepper.transaction.AddIncomeStep
import ru.telegram.bot.adapter.strategy.stepper.transaction.EnterAmountStep
import ru.telegram.bot.adapter.strategy.stepper.transaction.SelectCategoryStep

@ExtendWith(MockitoExtension::class)
class StepContextTest {
    @Mock
    lateinit var step1: AddIncomeStep

    @Mock
    lateinit var step2: SelectCategoryStep

    @Mock
    lateinit var step3: EnterAmountStep

    private lateinit var stepContext: StepContext

    private val chatId = CHAT_ID
    private val stepCode = StepCode.HELP
    private val nextStepCode = StepCode.FINAL

    @BeforeEach
    fun setup() {
        stepContext = StepContext(listOf(step1, step2, step3))
    }

    @Test
    fun `next should return next step code when step is found`() {
        whenever(step1.isAvailableForCurrentStep(stepCode)).thenReturn(false)
        whenever(step2.isAvailableForCurrentStep(stepCode)).thenReturn(true)
        whenever(step2.getNextStep(chatId)).thenReturn(nextStepCode)

        val result = stepContext.next(chatId, stepCode)

        assertAll(
            { verify(step1).isAvailableForCurrentStep(stepCode) },
            { verify(step2).isAvailableForCurrentStep(stepCode) },
            { verify(step3, never()).isAvailableForCurrentStep(stepCode) },
            { verify(step2).getNextStep(chatId) },
            { assertEquals(nextStepCode, result) }
        )
    }

    @Test
    fun `next should return null when no step is found`() {
        whenever(step1.isAvailableForCurrentStep(stepCode)).thenReturn(false)
        whenever(step2.isAvailableForCurrentStep(stepCode)).thenReturn(false)
        whenever(step3.isAvailableForCurrentStep(stepCode)).thenReturn(false)

        val result = stepContext.next(chatId, stepCode)

        assertAll(
            { verify(step1).isAvailableForCurrentStep(stepCode) },
            { verify(step2).isAvailableForCurrentStep(stepCode) },
            { verify(step3).isAvailableForCurrentStep(stepCode) },
            { verify(step1, never()).getNextStep(any()) },
            { verify(step2, never()).getNextStep(any()) },
            { verify(step3, never()).getNextStep(any()) },
            { assertNull(result) }
        )
    }

    @Test
    fun `next should return first matching step and not check remaining steps`() {
        whenever(step1.isAvailableForCurrentStep(stepCode)).thenReturn(true)
        whenever(step1.getNextStep(chatId)).thenReturn(nextStepCode)

        val result = stepContext.next(chatId, stepCode)

        assertAll(
            { verify(step1).isAvailableForCurrentStep(stepCode) },
            { verify(step2, never()).isAvailableForCurrentStep(stepCode) },
            { verify(step3, never()).isAvailableForCurrentStep(stepCode) },
            { verify(step1).getNextStep(chatId) },
            { assertEquals(nextStepCode, result) }
        )
    }

    @Test
    fun `next should work with empty step list`() {
        val emptyStepContext = StepContext(emptyList())

        val result = emptyStepContext.next(chatId, stepCode)

        assertNull(result)
    }
}