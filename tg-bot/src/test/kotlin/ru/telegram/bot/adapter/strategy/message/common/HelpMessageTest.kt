package ru.telegram.bot.adapter.strategy.message.common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.DataModel

/**
 * Test for contract of [AbstractSendMessage]
 */
@ExtendWith(MockitoExtension::class)
class HelpMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var helpMessage: HelpMessage

    private val formattedMsg = "HelpMessage"

    @BeforeEach
    fun setup() {
        helpMessage = HelpMessage(messageWriter)
    }

    @Test
    fun `message should call writer with correct stepCode and null data`() {
        whenever(messageWriter.process(any(), anyOrNull())).thenReturn(formattedMsg)

        val result = helpMessage.message()

        val captorStep = argumentCaptor<StepCode>()
        val captorData = argumentCaptor<DataModel>()

        assertAll(
            { verify(messageWriter).process(captorStep.capture(), captorData.capture()) },
            { assertEquals(helpMessage.classStepCode(), captorStep.firstValue) },
            { assertNull(captorData.firstValue) },
            { assertEquals(formattedMsg, result) }
        )
    }

    @Test
    fun `inlineButtons should return empty list`() {
        assertTrue(helpMessage.inlineButtons(CHAT_ID, null).isEmpty())
    }

    @Test
    fun `replyButtons should return empty list`() {
        assertTrue(helpMessage.replyButtons(CHAT_ID, null).isEmpty())
    }

    @Test
    fun `isPermitted should return true`() {
        assertTrue(helpMessage.isPermitted(CHAT_ID))
    }
}