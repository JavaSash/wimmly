package ru.telegram.bot.adapter.strategy.message.common

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
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.strategy.dto.ErrorDto

@ExtendWith(MockitoExtension::class)
class ErrorMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var errorMessage: ErrorMessage

    @BeforeEach
    fun setup() {
        errorMessage = ErrorMessage(messageWriter)
    }

    @Test
    fun `message should call writer with correct data and return result`() {
        val errorText = BotErrors.INVALID_AMOUNT.msg
        val dto = ErrorDto(errorText)

        whenever(messageWriter.process(eq(StepCode.ERROR), any())).thenReturn(FORMATTED_MSG)

        val result = errorMessage.message(dto)

        val captor = argumentCaptor<Map<String, Any?>>()

        assertAll(
            { verify(messageWriter).process(eq(StepCode.ERROR), captor.capture()) },
            { assertEquals(errorText, captor.firstValue["errorMsg"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should handle null dto`() {
        whenever(messageWriter.process(eq(StepCode.ERROR), any())).thenReturn(FORMATTED_MSG)

        val result = errorMessage.message(null)

        val captor = argumentCaptor<Map<String, Any?>>()

        assertAll(
            { verify(messageWriter).process(eq(StepCode.ERROR), captor.capture()) },
            {assertNull(captor.firstValue["errorMsg"]) },
            {assertEquals(FORMATTED_MSG, result) }
        )
    }
}