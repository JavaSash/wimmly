package ru.telegram.bot.adapter.strategy.message.report

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_150
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_50
import ru.telegram.bot.adapter.TestConstants.Tx.FORMATTED_MSG
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.BalanceDto
import ru.telegram.bot.adapter.utils.formatMoney

@ExtendWith(MockitoExtension::class)
class BalanceMessageTest {

    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var balanceMessage: BalanceMessage

    @BeforeEach
    fun setup() {
        balanceMessage = BalanceMessage(messageWriter)
    }

    @Test
    fun `message should map fields and call writer`() {
        val dto = BalanceDto(
            balance = AMOUNT_150,
            income = AMOUNT_100,
            expense = AMOUNT_50
        )
        whenever(messageWriter.process(eq(StepCode.BALANCE), any())).thenReturn(FORMATTED_MSG)

        val result = balanceMessage.message(dto)

        val captor = argumentCaptor<Map<String, String?>>()
        assertAll(
            { verify(messageWriter).process(eq(StepCode.BALANCE), captor.capture()) },
            { assertEquals(AMOUNT_150.formatMoney(), captor.firstValue["balance"]) },
            { assertEquals(AMOUNT_100.formatMoney(), captor.firstValue["income"]) },
            { assertEquals(AMOUNT_50.formatMoney(), captor.firstValue["expense"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should handle null dto`() {
        whenever(messageWriter.process(eq(StepCode.BALANCE), any())).thenReturn(FORMATTED_MSG)

        val result = balanceMessage.message(null)

        val captor = argumentCaptor<Map<String, String?>>()
        assertAll(
            { verify(messageWriter).process(eq(StepCode.BALANCE), captor.capture()) },
            { assertNull(captor.firstValue["balance"]) },
            { assertNull(captor.firstValue["income"]) },
            { assertNull(captor.firstValue["expense"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }
}