package ru.telegram.bot.adapter.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.ApplicationEventPublisher
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.dto.MessageModelDto
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.MessageContext
import ru.telegram.bot.adapter.strategy.StepContext
import ru.telegram.bot.adapter.strategy.dto.DataModel
import java.io.ByteArrayInputStream

@ExtendWith(MockitoExtension::class)
class MessageServiceTest {
    @Mock
    lateinit var telegramClient: TelegramClient

    @Mock
    lateinit var messageContext: MessageContext<DataModel>

    @Mock
    lateinit var applicationEventPublisher: ApplicationEventPublisher

    @Mock
    lateinit var stepContext: StepContext

    @InjectMocks
    lateinit var messageService: MessageService

    private val chatId = CHAT_ID
    private val msg = "msg"

    @Test
    fun `should send simple message when botPause = false, nextStep != null`() {
        val step = StepCode.START // botPause = false
        val nextStep = StepCode.BALANCE
        val message = MessageModelDto(
            message = msg,
            inlineButtons = emptyList(),
            replyButtons = emptyList()
        )
        whenever(messageContext.getMessage(chatId, step)).thenReturn(message)
        whenever(stepContext.next(chatId, step)).thenReturn(nextStep)

        messageService.sendMessageToBot(chatId, step)

        assertAll(
            { verify(telegramClient).execute(any<SendMessage>()) },
            { verify(stepContext).next(any(), any()) },
            { verify(applicationEventPublisher).publishEvent(any<TgStepMessageEvent>()) },
        )
    }

    @Test
    fun `should not publish event when botPause = true`() {
        val step = StepCode.ASK_COMMENT // botPause = true
        val message = MessageModelDto(
            message = msg,
            inlineButtons = emptyList(),
            replyButtons = emptyList()
        )
        whenever(messageContext.getMessage(chatId, step)).thenReturn(message)

        messageService.sendMessageToBot(chatId, step)

        assertAll(
            { verify(stepContext, never()).next(any(), any()) },
            { verify(applicationEventPublisher, never()).publishEvent(any()) },
        )
    }

    @Test
    fun `should send simple message when botPause = false, nextStep = null`() {
        val step = StepCode.START
        val message = MessageModelDto(
            message = msg,
            inlineButtons = emptyList(),
            replyButtons = emptyList()
        )
        whenever(messageContext.getMessage(chatId, step)).thenReturn(message)
        whenever(stepContext.next(chatId, step)).thenReturn(null)

        messageService.sendMessageToBot(chatId, step)
        assertAll(
            { verify(telegramClient).execute(any<SendMessage>()) },
            { verify(stepContext).next(any(), any()) },
            { verify(applicationEventPublisher, never()).publishEvent(any()) },
        )
    }

    @Test
    fun `should throw exception when message is null`() {
        val step = StepCode.START
        whenever(messageContext.getMessage(chatId, step)).thenReturn(null)

        assertThrows<IllegalStateException> {
            messageService.sendMessageToBot(chatId, step)
        }
    }

    @Test
    fun `should not send photo when next step = null`() {
        val step = StepCode.PHOTO // botPause = true
        val photo = MessageModelDto(
            message = msg,
            file = ByteArrayInputStream(msg.toByteArray()),
            inlineButtons = emptyList()
        )
        whenever(messageContext.getPhotoMessage(chatId, step)).thenReturn(photo)

        messageService.sendMessageToBot(chatId, step)

        assertAll(
            { verify(telegramClient).execute(any<SendPhoto>()) },
            { verify(stepContext, never()).next(any(), any()) },
            { verify(applicationEventPublisher, never()).publishEvent(any()) },
        )
    }

    @Test
    fun `should throw exception when photo message is null`() {
        val step = StepCode.PHOTO

        whenever(messageContext.getPhotoMessage(chatId, step)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            messageService.sendMessageToBot(chatId, step)
        }
    }

    @Test
    fun `should skip message for NO_MESSAGE step`() {
        val step = StepCode.FINAL // тип NO_MESSAGE
        whenever(stepContext.next(chatId, step)).thenReturn(null)

        messageService.sendMessageToBot(chatId, step)
        assertAll(
            { verify(telegramClient, never()).execute(any<SendMessage>()) },
            { verify(stepContext).next(any(), any()) },
            { verify(applicationEventPublisher, never()).publishEvent(any()) },
        )
    }


}