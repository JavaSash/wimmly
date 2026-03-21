package ru.telegram.bot.adapter.strategy.message.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Button.YES_NO_BUTTONS
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.AskYesNoDto
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

@ExtendWith(MockitoExtension::class)
class AskCommentMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var askCommentMessage: AskCommentMessage

    @BeforeEach
    fun setup() {
        askCommentMessage = AskCommentMessage(messageWriter)
    }

    @Test
    fun `inlineButtons should return two buttons with correct positions and texts`() {
        val chatId = CHAT_ID
        val data = YES_NO_BUTTONS

        val result = askCommentMessage.inlineButtons(chatId, data)

        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(0, result[0].rowPos) },
            { assertEquals(YES, result[0].text) },
            { assertEquals(1, result[1].rowPos) },
            { assertEquals(NO, result[1].text) }
        )
    }

    @Test
    fun `inlineButtons should handle different accept texts`() {
        val confirm = "Confirm"
        val cancel = "Cancel"
        val chatId = CHAT_ID
        val data = AskYesNoDto(accept = listOf(confirm, cancel))

        val result = askCommentMessage.inlineButtons(chatId, data)

        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(confirm, result[0].text) },
            { assertEquals(cancel, result[1].text) }
        )
    }
}