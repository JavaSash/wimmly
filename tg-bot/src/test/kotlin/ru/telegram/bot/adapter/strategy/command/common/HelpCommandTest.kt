package ru.telegram.bot.adapter.strategy.command.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.command.CommandBasicTest

/**
 * @link [AbstractCommand] test
 */
class HelpCommandTest: CommandBasicTest() {

    private lateinit var command: HelpCommand

    @BeforeEach
    fun setup() {
        command = HelpCommand(
            chatContextRepository,
            applicationEventPublisher,
            userService,
            transactionDraftRepository,
            searchContextRepository
        )
    }

    @Test
    fun `execute should publish help step event when user fully exists`() {
        mockUserFullyExists()
        val chatId = CHAT_ID

        command.execute(telegramClient, user, chat, emptyArray())

        val captor = argumentCaptor<TgStepMessageEvent>()

        verify(applicationEventPublisher).publishEvent(captor.capture())
        val event = captor.firstValue

        assertAll(
            { verify(chatContextRepository, never()).createUser(any()) },
            { verify(transactionDraftRepository, never()).createTransactionDraft(any()) },
            { verify(searchContextRepository, never()).createSearchContext(any()) },
            { verify(userService, never()).syncUserToBackend(any(), any()) },
            { verify(applicationEventPublisher).publishEvent(any<TgStepMessageEvent>()) },
            { assertEquals(chatId, event.chatId) },
            { assertEquals(command.classStepCode(), event.stepCode) }
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [-0, -5, 0])
    fun `execute should do nothing for chatId less or equal than 0`(chatId: Int) {
        val groupChat = Chat.builder().id(chatId.toLong()).type("private").build()
        val negativeUser = User(chatId.toLong(), "Test", false)

        command.execute(telegramClient, negativeUser, groupChat, emptyArray())

        assertAll(
            { verify(applicationEventPublisher, never()).publishEvent(any()) },
            { verify(chatContextRepository, never()).isUserExist(any()) },
            { verify(chatContextRepository, never()).createUser(any()) },
            { verify(transactionDraftRepository, never()).createTransactionDraft(any()) },
            { verify(searchContextRepository, never()).createSearchContext(any()) },
            { verify(userService, never()).syncUserToBackend(any(), any()) },
        )
    }

    @Test
    fun `execute should create user and related contexts when user not exist`() {
        mockUserNotExists()
        val chatId = CHAT_ID

        command.execute(telegramClient, user, chat, emptyArray())

        assertAll(
            { verify(chatContextRepository).createUser(chatId) },
            { verify(transactionDraftRepository).createTransactionDraft(chatId) },
            { verify(searchContextRepository).createSearchContext(chatId) },
            { verify(userService).syncUserToBackend(chatId, user) },
            { verify(applicationEventPublisher).publishEvent(any<TgStepMessageEvent>()) }
        )
    }

    @Test
    fun `execute should sync user to backend when not exist in backend`() {
        mockUserNotInBackend()
        val chatId = CHAT_ID

        command.execute(telegramClient, user, chat, emptyArray())

        assertAll(
            { verify(chatContextRepository, never()).createUser(any()) },
            { verify(transactionDraftRepository, never()).createTransactionDraft(any()) },
            { verify(searchContextRepository, never()).createSearchContext(any()) },
            { verify(userService).syncUserToBackend(chatId, user) },
            { verify(applicationEventPublisher).publishEvent(any<TgStepMessageEvent>()) }
        )
    }
}