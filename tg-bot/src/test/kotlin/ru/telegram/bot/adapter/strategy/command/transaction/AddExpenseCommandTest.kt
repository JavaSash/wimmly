package ru.telegram.bot.adapter.strategy.command.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.command.CommandBasicTest
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE

class AddExpenseCommandTest : CommandBasicTest() {

    private lateinit var command: AddExpenseCommand

    @BeforeEach
    fun setup() {
        command = AddExpenseCommand(
            chatContextRepository,
            userService,
            applicationEventPublisher,
            transactionDraftRepository,
            searchContextRepository
        )
        stepCode = StepCode.ADD_EXPENSE
    }

    @Test
    fun `execute should update transaction type and flow when user fully exists`() {
        val chatId = CHAT_ID
        mockUserFullyExists(chatId)

        command.execute(telegramClient, user, chat, emptyArray())

        val captor = argumentCaptor<TgStepMessageEvent>()
        verify(applicationEventPublisher).publishEvent(captor.capture())

        val event = captor.firstValue

        assertAll(
            { verify(transactionDraftRepository).updateTransactionType(chatId, EXPENSE) },
            { verify(chatContextRepository).updateFlowContext(chatId, stepCode.name) },
            { verify(chatContextRepository, never()).createUser(any()) },
            { verify(transactionDraftRepository, never()).createTransactionDraft(any()) },
            { verify(searchContextRepository, never()).createSearchContext(any()) },
            { assertEquals(chatId, event.chatId) },
            { assertEquals(stepCode, event.stepCode) }
        )
    }

    @Test
    fun `execute should create user and then update transaction type when user not exists`() {
        val chatId = CHAT_ID
        mockUserNotExists(chatId)

        command.execute(telegramClient, user, chat, emptyArray())

        val captor = argumentCaptor<TgStepMessageEvent>()
        verify(applicationEventPublisher).publishEvent(captor.capture())

        val event = captor.firstValue

        assertAll(
            { verify(chatContextRepository).createUser(chatId) },
            { verify(transactionDraftRepository).createTransactionDraft(chatId) },
            { verify(searchContextRepository).createSearchContext(chatId) },
            { verify(userService).syncUserToBackend(chatId, user) },
            { verify(transactionDraftRepository).updateTransactionType(chatId, EXPENSE) },
            { verify(chatContextRepository).updateFlowContext(chatId, stepCode.name) },
            { assertEquals(chatId, event.chatId) },
            { assertEquals(stepCode, event.stepCode) }
        )
    }
}