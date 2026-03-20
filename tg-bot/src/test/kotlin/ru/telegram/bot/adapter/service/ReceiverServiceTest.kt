package ru.telegram.bot.adapter.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.domain.tables.tables.pojos.ChatContext
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgReceivedCallbackEvent
import ru.telegram.bot.adapter.event.TgReceivedMessageEvent
import ru.telegram.bot.adapter.repository.ChatContextRepository

@ExtendWith(MockitoExtension::class)
class ReceiverServiceTest {
    @Mock
    lateinit var applicationEventPublisher: ApplicationEventPublisher
    @Mock
    lateinit var chatCtxRepo: ChatContextRepository
    @InjectMocks
    lateinit var receiverService: ReceiverService

    @Test
    fun `execute should publish message event`() {
        val update = mock<Update>()
        val message = mock<Message>()
        whenever(update.hasCallbackQuery()).thenReturn(false)
        whenever(update.hasMessage()).thenReturn(true)
        whenever(update.message).thenReturn(message)
        whenever(message.chatId).thenReturn(CHAT_ID)
        whenever(chatCtxRepo.getUser(CHAT_ID)).thenReturn(ChatContext(id = CHAT_ID, stepCode = StepCode.START.name))

        receiverService.execute(update)

        val captor = argumentCaptor<TgReceivedMessageEvent>()

        verify(applicationEventPublisher).publishEvent(captor.capture())

        val event = captor.firstValue

        assertAll(
            { assertEquals(CHAT_ID, event.chatId) },
            { assertEquals(StepCode.START, event.stepCode) },
            { assertEquals(message, event.message) }
        )
    }

    @Test
    fun `execute should publish callback event`() {
        val update = mock<Update>()
        val callback = mock<CallbackQuery>()
        val from = mock<org.telegram.telegrambots.meta.api.objects.User>()
        whenever(update.hasCallbackQuery()).thenReturn(true)
        whenever(update.callbackQuery).thenReturn(callback)
        whenever(callback.from).thenReturn(from)
        whenever(from.id).thenReturn(CHAT_ID)
        whenever(chatCtxRepo.getUser(CHAT_ID)).thenReturn(ChatContext(id = CHAT_ID, stepCode = StepCode.START.name))
        receiverService.execute(update)

        val eventCaptor = argumentCaptor<TgReceivedCallbackEvent>()

        verify(applicationEventPublisher).publishEvent(eventCaptor.capture())

        assertAll(
            { assertEquals(CHAT_ID, eventCaptor.firstValue.chatId) },
            { assertEquals(StepCode.START, eventCaptor.firstValue.stepCode) },
            { assertEquals(callback, eventCaptor.firstValue.callback) }
        )
    }

    @Test
    fun `execute should throw exception for unsupported update`() {
        val update = mock<Update>()

        whenever(update.hasCallbackQuery()).thenReturn(false)
        whenever(update.hasMessage()).thenReturn(false)

        assertThrows<IllegalStateException> {
            receiverService.execute(update)
        }

        verify(applicationEventPublisher, never()).publishEvent(any())
    }
}