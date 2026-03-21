package ru.telegram.bot.adapter.strategy.message.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Tx.FORMATTED_MSG
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formShowTransactionsDto
import ru.telegram.bot.adapter.formTransactionItem
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.ShowTransactionsDto
import ru.telegram.bot.adapter.strategy.dto.TransactionItem

@ExtendWith(MockitoExtension::class)
class ShowTransactionsMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var showTransactionsMessage: ShowTransactionsMessage

    @BeforeEach
    fun setup() {
        showTransactionsMessage = ShowTransactionsMessage(messageWriter)
    }

    @Test
    fun `message should call writer with correct data and return result`() {
        val dto = formShowTransactionsDto(listOf(formTransactionItem()))

        whenever(messageWriter.process(eq(StepCode.SHOW_TRANSACTIONS), any())).thenReturn(FORMATTED_MSG)

        val result = showTransactionsMessage.message(dto)

        val captor = argumentCaptor<Map<String, Any?>>()

        assertAll(
            { verify(messageWriter).process(eq(StepCode.SHOW_TRANSACTIONS), captor.capture()) },
            { assertEquals(dto, captor.firstValue["data"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should handle null dto`() {
        whenever(messageWriter.process(eq(StepCode.SHOW_TRANSACTIONS), any())).thenReturn(FORMATTED_MSG)

        val result = showTransactionsMessage.message(null)

        val captor = argumentCaptor<Map<String, Any?>>()

        assertAll(
            { verify(messageWriter).process(eq(StepCode.SHOW_TRANSACTIONS), captor.capture()) },
            { assertNull(captor.firstValue["data"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should handle dto with empty transactions list`() {
        val dto = formShowTransactionsDto(emptyList())

        whenever(messageWriter.process(eq(StepCode.SHOW_TRANSACTIONS), any())).thenReturn(FORMATTED_MSG)

        val result = showTransactionsMessage.message(dto)

        val captor = argumentCaptor<Map<String, Any?>>()

        assertAll(
            { verify(messageWriter).process(eq(StepCode.SHOW_TRANSACTIONS), captor.capture()) },
            { assertEquals(dto, captor.firstValue["data"]) },
            { assertEquals(emptyList<TransactionItem>(), (captor.firstValue["data"] as ShowTransactionsDto).transactions) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }
}