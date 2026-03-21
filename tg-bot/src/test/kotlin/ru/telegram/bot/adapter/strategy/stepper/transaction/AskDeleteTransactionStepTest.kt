package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.repository.ChatContextRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify

@ExtendWith(MockitoExtension::class)
class AskDeleteTransactionStepTest {
    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    private lateinit var askDeleteTransactionStep: AskDeleteTransactionStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        askDeleteTransactionStep = AskDeleteTransactionStep(chatContextRepository)
    }

    @Test
    fun `getNextStep should return REMOVE_TRANSACTION when user accept is true`() {
        val user = formChatContext(accept = true)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = askDeleteTransactionStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.REMOVE_TRANSACTION, result) }
        )
    }

    @Test
    fun `getNextStep should return FINAL when user accept is false`() {
        val user = formChatContext(accept = false)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = askDeleteTransactionStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.FINAL, result) }
        )
    }

    @Test
    fun `getNextStep should return null when user is null`() {
        whenever(chatContextRepository.getUser(chatId)).thenReturn(null)

        val result = askDeleteTransactionStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertNull(result) }
        )
    }
}