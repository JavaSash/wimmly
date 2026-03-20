package ru.telegram.bot.adapter.strategy.command.transaction

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.command.CommandBasicTest

class SearchTransactionsCommandTest: CommandBasicTest() {

    private lateinit var command: SearchTransactionsCommand

    @BeforeEach
    fun setup() {
        command = SearchTransactionsCommand(
            chatContextRepository,
            userService,
            applicationEventPublisher,
            transactionDraftRepository,
            searchContextRepository
        )
        stepCode = StepCode.SEARCH_TRANSACTIONS
    }

    @Test
    fun `execute should update flow when user fully exists`() {
        val chatId = CHAT_ID
        mockUserFullyExists(chatId)

        command.execute(telegramClient, user, chat, emptyArray())

        val captor = argumentCaptor<TgStepMessageEvent>()
        verify(applicationEventPublisher).publishEvent(captor.capture())

        val event = captor.firstValue

        assertAll(
            { verify(chatContextRepository).updateFlowContext(chatId, stepCode.name) },
            { verify(chatContextRepository, never()).createUser(any()) },
            { verify(transactionDraftRepository, never()).createTransactionDraft(any()) },
            { verify(searchContextRepository, never()).createSearchContext(any()) },
            { Assertions.assertEquals(chatId, event.chatId) },
            { Assertions.assertEquals(stepCode, event.stepCode) }
        )
    }

    @Test
    fun `execute should create user and then update flow when user not exists`() {
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
            { verify(chatContextRepository).updateFlowContext(chatId, stepCode.name) },
            { Assertions.assertEquals(chatId, event.chatId) },
            { Assertions.assertEquals(stepCode, event.stepCode) }
        )
    }
}