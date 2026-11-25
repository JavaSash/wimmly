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
import ru.telegram.bot.adapter.TestConstants.Tx.FORMATTED_MSG
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.dto.BalanceDto

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
            balance = "-1234.50".toBigDecimal(),
            income = "1234567.75".toBigDecimal(),
            expense = "1235802.25".toBigDecimal()
        )
        whenever(messageWriter.process(eq(StepCode.BALANCE), any())).thenReturn(FORMATTED_MSG)

        val result = balanceMessage.message(dto)

        val captor = argumentCaptor<Map<String, String?>>()
        assertAll(
            { verify(messageWriter).process(eq(StepCode.BALANCE), captor.capture()) },
            { assertEquals("-1\u00A0234.50 ₽", captor.firstValue["balance"]) },
            { assertEquals("1\u00A0234\u00A0567.75 ₽", captor.firstValue["income"]) },
            { assertEquals("1\u00A0235\u00A0802.25 ₽", captor.firstValue["expense"]) },
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
