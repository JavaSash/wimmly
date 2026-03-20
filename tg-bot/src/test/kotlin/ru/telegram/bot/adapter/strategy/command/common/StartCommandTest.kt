package ru.telegram.bot.adapter.strategy.command.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.command.CommandBasicTest
import ru.telegram.bot.adapter.strategy.command.report.BalanceCommand

class StartCommandTest: CommandBasicTest() {
    @Mock
    lateinit var balanceCommand: BalanceCommand

    private lateinit var command: StartCommand

    @BeforeEach
    fun setup() {
        command = StartCommand(
            chatContextRepository,
            balanceCommand,
            applicationEventPublisher,
            userService,
            transactionDraftRepository,
            searchContextRepository
        )
    }

    @Test
    fun `execute should call balanceCommand when user exists`() {
        mockBotUserExists()

        command.execute(telegramClient, user, chat, emptyArray())

        assertAll(
            { verify(balanceCommand).execute(telegramClient, user, chat, emptyArray()) },
            { verify(applicationEventPublisher, never()).publishEvent(any()) },
            { verify(chatContextRepository, never()).createUser(any()) }
        )
    }

    @Test
    fun `execute should create user and publish START event when user not exists`() {
        val chatId = CHAT_ID
        mockUserNotExists()

        command.execute(telegramClient, user, chat, emptyArray())

        val captor = argumentCaptor<TgStepMessageEvent>()

        verify(applicationEventPublisher).publishEvent(captor.capture())

        val event = captor.firstValue

        assertAll(
            { verify(balanceCommand, never()).execute(any(), any(), any(), any()) },
            { verify(chatContextRepository).createUser(chatId) },
            { verify(transactionDraftRepository).createTransactionDraft(chatId) },
            { verify(searchContextRepository).createSearchContext(chatId) },
            { verify(userService).syncUserToBackend(chatId, user) },
            { assertEquals(chatId, event.chatId) },
            { assertEquals(StepCode.START, event.stepCode) }
        )
    }
}