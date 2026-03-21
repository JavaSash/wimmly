package ru.telegram.bot.adapter.strategy.stepper.common

import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository

@ExtendWith(MockitoExtension::class)
class FinalStepTest {
    @Mock
    lateinit var chatContextRepository: ChatContextRepository
    @Mock
    lateinit var transactionDraftRepository: TransactionDraftRepository
    @Mock
    lateinit var searchContextRepository: SearchContextRepository

    private lateinit var finalStep: FinalStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        finalStep = FinalStep(
            chatContextRepository,
            transactionDraftRepository,
            searchContextRepository
        )
    }

    @Test
    fun `getNextStep should clear all dialog states and return null`() {
        doNothing().`when`(chatContextRepository).clearDialogState(chatId)
        doNothing().`when`(transactionDraftRepository).clearDialogState(chatId)
        doNothing().`when`(searchContextRepository).clearDialogState(chatId)

        val result = finalStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).clearDialogState(chatId) },
            { verify(transactionDraftRepository).clearDialogState(chatId) },
            { verify(searchContextRepository).clearDialogState(chatId) },
            { assertNull(result) }
        )
    }

    @Test
    fun `getNextStep should return null even when clearing fails`() {
        whenever(chatContextRepository.clearDialogState(chatId)).thenThrow(RuntimeException("Database error"))
        doNothing().`when`(transactionDraftRepository).clearDialogState(chatId)
        doNothing().`when`(searchContextRepository).clearDialogState(chatId)

        val result = finalStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).clearDialogState(chatId) },
            { verify(transactionDraftRepository).clearDialogState(chatId) },
            { verify(searchContextRepository).clearDialogState(chatId) },
            { assertNull(result) }
        )
    }

    @Test
    fun `getNextStep should attempt to clear all repositories even if first fails`() {
        whenever(chatContextRepository.clearDialogState(chatId)).thenThrow(RuntimeException("Database error"))
        whenever(transactionDraftRepository.clearDialogState(chatId)).thenThrow(RuntimeException("Database error"))
        whenever(searchContextRepository.clearDialogState(chatId)).thenThrow(RuntimeException("Database error"))

        val result = finalStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).clearDialogState(chatId) },
            { verify(transactionDraftRepository).clearDialogState(chatId) },
            { verify(searchContextRepository).clearDialogState(chatId) },
            { assertNull(result) }
        )
    }
}