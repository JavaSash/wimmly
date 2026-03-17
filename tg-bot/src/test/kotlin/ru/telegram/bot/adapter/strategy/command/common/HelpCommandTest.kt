package ru.telegram.bot.adapter.strategy.command.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.UserService

/**
 * @link [AbstractCommand] test
 */
@ExtendWith(MockitoExtension::class)
class HelpCommandTest {
    @Mock lateinit var chatContextRepository: ChatContextRepository
    @Mock
    lateinit var applicationEventPublisher: ApplicationEventPublisher
    @Mock
    lateinit var userService: UserService
    @Mock
    lateinit var transactionDraftRepository: TransactionDraftRepository
    @Mock
    lateinit var searchContextRepository: SearchContextRepository
    @Mock
    lateinit var telegramClient: TelegramClient

    lateinit var command: HelpCommand

    private val chat = Chat.builder().id(CHAT_ID).type("private").build()
    private val user = User(CHAT_ID, "Test", false)

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
        val chatId = CHAT_ID
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(true)
        whenever(userService.isExist(chatId)).thenReturn(true)
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
        val chatId = CHAT_ID
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(false)

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
        val chatId = CHAT_ID
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(true)
        whenever(userService.isExist(chatId)).thenReturn(false)

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