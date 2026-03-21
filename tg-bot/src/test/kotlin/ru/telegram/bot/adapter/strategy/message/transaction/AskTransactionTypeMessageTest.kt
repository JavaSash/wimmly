package ru.telegram.bot.adapter.strategy.message.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.AskYesNoDto
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@ExtendWith(MockitoExtension::class)
class AskTransactionTypeMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var askTransactionTypeMessage: AskTransactionTypeMessage

    @BeforeEach
    fun setup() {
        askTransactionTypeMessage = AskTransactionTypeMessage(messageWriter)
    }

    @Test
    fun `message should return transaction type selection text`() {
        val result = askTransactionTypeMessage.message(null)

        assertEquals("📊 <b>Выберите тип транзакции:</b>", result)
    }

    @Test
    fun `inlineButtons should return income and expense buttons on same row`() {
        val chatId = CHAT_ID
        val data = AskYesNoDto(accept = emptyList()) // stub

        val result = askTransactionTypeMessage.inlineButtons(chatId, data)

        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(0, result[0].rowPos) },
            { assertEquals(INCOME, result[0].text) },
            { assertEquals(0, result[1].rowPos) },
            { assertEquals(EXPENSE, result[1].text) }
        )
    }

    @Test
    fun `inlineButtons should work with null data`() {
        val chatId = CHAT_ID

        val result = askTransactionTypeMessage.inlineButtons(chatId, null)

        assertAll(
            { assertEquals(2, result.size) },
            { assertEquals(INCOME, result[0].text) },
            { assertEquals(EXPENSE, result[1].text) }
        )
    }
}