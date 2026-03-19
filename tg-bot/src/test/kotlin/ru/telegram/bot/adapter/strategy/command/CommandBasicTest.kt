package ru.telegram.bot.adapter.strategy.command

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.UserService

@ExtendWith(MockitoExtension::class)
abstract class CommandBasicTest {
    @Mock
    protected lateinit var chatContextRepository: ChatContextRepository
    @Mock
    protected lateinit var applicationEventPublisher: ApplicationEventPublisher
    @Mock
    protected lateinit var userService: UserService
    @Mock
    protected lateinit var transactionDraftRepository: TransactionDraftRepository
    @Mock
    protected lateinit var searchContextRepository: SearchContextRepository
    @Mock
    protected lateinit var telegramClient: TelegramClient

    protected val chat: Chat = Chat.builder()
        .id(CHAT_ID)
        .type("private")
        .build()

    protected val user: User = User(CHAT_ID, "Test", false)

    protected fun mockUserExists(chatId: Long = CHAT_ID) {
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(true)
    }

    protected fun mockUserFullyExists(chatId: Long = CHAT_ID) {
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(true)
        whenever(userService.isExist(chatId)).thenReturn(true)
    }

    protected fun mockUserNotExists(chatId: Long = CHAT_ID) {
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(false)
    }

    protected fun mockUserNotInBackend(chatId: Long = CHAT_ID) {
        whenever(chatContextRepository.isUserExist(chatId)).thenReturn(true)
        whenever(userService.isExist(chatId)).thenReturn(false)
    }
}