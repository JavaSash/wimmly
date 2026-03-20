package ru.telegram.bot.adapter.service

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.DbBasicTest
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgReceivedCallbackEvent
import ru.telegram.bot.adapter.event.TgReceivedMessageEvent
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.LogicContext
import ru.telegram.bot.adapter.strategy.StepContext

class ApplicationListenerTest : DbBasicTest() {

    private lateinit var listener: ApplicationListener

    @Mock
    private lateinit var logicContext: LogicContext

    @Mock
    private lateinit var stepContext: StepContext

    @Mock
    private lateinit var messageService: MessageService

    @BeforeEach
    fun setup() {
        listener = ApplicationListener(
            logicContext = logicContext,
            stepContext = stepContext,
            chatContextRepository = chatCtxRepo,
            messageService = messageService
        )
        chatCtxRepo.createUser(CHAT_ID)
    }

    @Nested
    inner class MessageTest {

        @Test
        fun `should handle text message and trigger next step`() {
            val event = TgReceivedMessageEvent(
                chatId = CHAT_ID,
                message = mock(),
                stepCode = StepCode.START
            )
            val nextStep = StepCode.BALANCE

            whenever(stepContext.next(event.chatId, event.stepCode)).thenReturn(nextStep)

            listener.messageBean().onApplicationEvent(event)

            val user = chatCtxRepo.getUser(event.chatId)
            assertAll(
                { verify(logicContext).execute(event.chatId, event.message, event.stepCode) },
                { verify(stepContext).next(event.chatId, event.stepCode) },
                { assertEquals(nextStep.name, user?.stepCode) },
                { verify(messageService).sendMessageToBot(event.chatId, nextStep) }
            )
        }

        @Test
        fun `should not send message when nextStep is null`() {
            val event = TgReceivedMessageEvent(
                chatId = CHAT_ID,
                message = mock(),
                stepCode = StepCode.START
            )

            whenever(stepContext.next(event.chatId, event.stepCode)).thenReturn(null)

            listener.messageBean().onApplicationEvent(event)

            assertAll(
                { verify(logicContext).execute(event.chatId, event.message, event.stepCode) },
                { verify(stepContext).next(event.chatId, event.stepCode) },
                { verify(messageService, never()).sendMessageToBot(any(), any()) }
            )
        }
    }

    @Nested
    inner class StepMessageTest {

        @Test
        fun `should update step in database and send message`() {
            val event = TgStepMessageEvent(
                chatId = CHAT_ID,
                stepCode = StepCode.BALANCE
            )

            listener.stepMessageBean().onApplicationEvent(event)

            val user = chatCtxRepo.getUser(event.chatId)
            assertAll(
                { assertEquals(event.stepCode.name, user?.stepCode) },
                { verify(messageService).sendMessageToBot(event.chatId, event.stepCode) }
            )
        }

        @Test
        fun `should handle multiple step updates`() {
            val event1 = TgStepMessageEvent(
                chatId = CHAT_ID,
                stepCode = StepCode.ENTER_AMOUNT
            )
            val event2 = TgStepMessageEvent(
                chatId = event1.chatId,
                stepCode = StepCode.ASK_COMMENT
            )

            listener.stepMessageBean().onApplicationEvent(event1)
            listener.stepMessageBean().onApplicationEvent(event2)

            val user = chatCtxRepo.getUser(event1.chatId)

            assertAll(
                { assertEquals(event2.stepCode.name, user?.stepCode) },
                { verify(messageService).sendMessageToBot(event1.chatId, event1.stepCode) },
                { verify(messageService).sendMessageToBot(event2.chatId, event2.stepCode) }
            )
        }
    }

    @Nested
    inner class CallbackMessageTest {

        @Test
        fun `should handle callback with FINAL status and trigger next step`() {
            val event = TgReceivedCallbackEvent(
                chatId = CHAT_ID,
                callback = mock(),
                stepCode = StepCode.SELECT_CATEGORY
            )
            val nextStep = StepCode.ENTER_AMOUNT

            whenever(logicContext.execute(event.chatId, event.callback, event.stepCode)).thenReturn(ExecuteStatus.FINAL)
            whenever(stepContext.next(event.chatId, event.stepCode)).thenReturn(nextStep)

            listener.callbackMessageBean().onApplicationEvent(event)

            val user = chatCtxRepo.getUser(event.chatId)
            assertAll(
                { verify(logicContext).execute(event.chatId, event.callback, event.stepCode) },
                { verify(stepContext).next(event.chatId, event.stepCode) },
                { assertEquals(nextStep.name, user?.stepCode) },
                { verify(messageService).sendMessageToBot(event.chatId, nextStep) }
            )
        }

        @Test
        fun `should throw when callback returns NOTHING`() {
            val event = TgReceivedCallbackEvent(
                chatId = CHAT_ID,
                callback = mock(),
                stepCode = StepCode.SELECT_CATEGORY
            )

            whenever(
                logicContext.execute(
                    event.chatId,
                    event.callback,
                    event.stepCode
                )
            ).thenReturn(ExecuteStatus.NOTHING)

            assertThrows<IllegalStateException> { listener.callbackMessageBean().onApplicationEvent(event) }
        }

        @Test
        fun `should not send message when nextStep is null after FINAL`() {
            val event = TgReceivedCallbackEvent(
                chatId = CHAT_ID,
                callback = mock(),
                stepCode = StepCode.SELECT_CATEGORY
            )

            whenever(logicContext.execute(event.chatId, event.callback, event.stepCode)).thenReturn(ExecuteStatus.FINAL)
            whenever(stepContext.next(event.chatId, event.stepCode)).thenReturn(null)

            listener.callbackMessageBean().onApplicationEvent(event)

            assertAll(
                { verify(logicContext).execute(event.chatId, event.callback, event.stepCode) },
                { verify(stepContext).next(event.chatId, event.stepCode) },
                { verify(messageService, never()).sendMessageToBot(any(), any()) }
            )
        }
    }
}