package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.repository.ChatContextRepository

@ExtendWith(MockitoExtension::class)
class AskDateStepTest {
    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    private lateinit var askDateStep: AskDateStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        askDateStep = AskDateStep(chatContextRepository)
    }

    @Test
    fun `getNextStep should return ENTER_DATE when user accept is true`() {
        val user = formChatContext(accept = true)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = askDateStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.ENTER_DATE, result) }
        )
    }

    @Test
    fun `getNextStep should return ASK_COMMENT when user accept is false`() {
        val user = formChatContext(accept = false)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = askDateStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.ASK_COMMENT, result) }
        )
    }

    @Test
    fun `getNextStep should return null when user is null`() {
        whenever(chatContextRepository.getUser(chatId)).thenReturn(null)

        val result = askDateStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertNull(result) }
        )
    }
}