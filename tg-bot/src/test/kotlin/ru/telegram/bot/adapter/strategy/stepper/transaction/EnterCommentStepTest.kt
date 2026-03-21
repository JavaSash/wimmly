package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.ErrorService

@ExtendWith(MockitoExtension::class)
class EnterCommentStepTest {
    @Mock
    lateinit var errorService: ErrorService

    private lateinit var enterCommentStep: EnterCommentStep

    private val chatId = CHAT_ID
    private val expectedStep = StepCode.CREATE_TRANSACTION

    @BeforeEach
    fun setup() {
        enterCommentStep = EnterCommentStep(errorService)
    }

    @Test
    fun `getNextStep should delegate to errorService with CREATE_TRANSACTION as success step`() {
        whenever(errorService.resolveNextStep(chatId, expectedStep)).thenReturn(expectedStep)

        val result = enterCommentStep.getNextStep(chatId)

        assertAll(
            { verify(errorService).resolveNextStep(chatId, expectedStep) },
            { assertEquals(expectedStep, result) }
        )
    }
}