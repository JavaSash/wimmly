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
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.repository.ChatContextRepository

@ExtendWith(MockitoExtension::class)
class ShowTransactionsStepTest {
    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    private lateinit var showTransactionsStep: ShowTransactionsStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        showTransactionsStep = ShowTransactionsStep(chatContextRepository)
    }

    @Test
    fun `getNextStep should return ASK_DELETE_TRANSACTION when flowContext is DELETE_TRANSACTION`() {
        val user = formChatContext(flowContext = StepCode.DELETE_TRANSACTION.name)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = showTransactionsStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.ASK_DELETE_TRANSACTION, result) }
        )
    }

    @Test
    fun `getNextStep should return FINAL when flowContext is SEARCH_TRANSACTIONS`() {
        val user = formChatContext(flowContext = StepCode.SEARCH_TRANSACTIONS.name)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = showTransactionsStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.FINAL, result) }
        )
    }

    @Test
    fun `getNextStep should return FINAL when flowContext is null`() {
        val user = formChatContext(flowContext = null)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = showTransactionsStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.FINAL, result) }
        )
    }

    @Test
    fun `getNextStep should return FINAL when user is null`() {
        whenever(chatContextRepository.getUser(chatId)).thenReturn(null)

        val result = showTransactionsStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.FINAL, result) }
        )
    }
}