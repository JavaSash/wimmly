package ru.telegram.bot.adapter.strategy

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.TestConstants.Chat.TEXT
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formCallbackQuery
import ru.telegram.bot.adapter.formMessage
import ru.telegram.bot.adapter.strategy.logic.common.CallbackChooser
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@ExtendWith(MockitoExtension::class)
class LogicContextTest {
    @Mock
    lateinit var telegramCallbackChooser: Map<StepCode, CallbackChooser>

    @Mock
    lateinit var telegramMessageChooser: Map<StepCode, MessageChooser>

    @Mock
    lateinit var callbackChooser: CallbackChooser

    @Mock
    lateinit var messageChooser: MessageChooser

    private lateinit var logicContext: LogicContext

    private val chatId = CHAT_ID
    private val stepCode = StepCode.ADD_INCOME

    @BeforeEach
    fun setup() {
        logicContext = LogicContext(telegramCallbackChooser, telegramMessageChooser)
    }

    @Test
    fun `execute with message should call messageChooser for given stepCode`() {
        val message = formMessage(text = TEXT)

        whenever(telegramMessageChooser[stepCode]).thenReturn(messageChooser)
        doNothing().`when`(messageChooser).execute(chatId, message)

        logicContext.execute(chatId, message, stepCode)

        assertAll(
            { verify(telegramMessageChooser)[stepCode] },
            { verify(messageChooser).execute(chatId, message) }
        )
    }

    @Test
    fun `execute with message should not call messageChooser when stepCode not found`() {
        val message = formMessage(text = TEXT)

        whenever(telegramMessageChooser[stepCode]).thenReturn(null)

        logicContext.execute(chatId, message, stepCode)

        assertAll(
            { verify(telegramMessageChooser)[stepCode] },
            { verify(messageChooser, never()).execute(any(), any()) }
        )
    }

    @Test
    fun `execute with callback should return callbackChooser result`() {
        val callbackQuery = formCallbackQuery(data = "some_data")
        val expectedStatus = ExecuteStatus.FINAL

        whenever(telegramCallbackChooser[stepCode]).thenReturn(callbackChooser)
        whenever(callbackChooser.execute(chatId, callbackQuery)).thenReturn(expectedStatus)

        val result = logicContext.execute(chatId, callbackQuery, stepCode)

        assertAll(
            { verify(telegramCallbackChooser)[stepCode] },
            { verify(callbackChooser).execute(chatId, callbackQuery) },
            { assertEquals(expectedStatus, result) }
        )
    }

    @Test
    fun `execute with callback should throw IllegalStateException when callbackChooser not found`() {
        val callbackQuery = formCallbackQuery(data = "some_data")

        whenever(telegramCallbackChooser[stepCode]).thenReturn(null)

        assertThrows<IllegalStateException> { logicContext.execute(chatId, callbackQuery, stepCode) }

        assertAll(
            { verify(telegramCallbackChooser)[stepCode] },
            { verify(callbackChooser, never()).execute(any(), any()) }
        )
    }

    @Test
    fun `execute with callback should throw IllegalStateException with correct message`() {
        val callbackQuery = formCallbackQuery(data = "some_data")

        whenever(telegramCallbackChooser[stepCode]).thenReturn(null)

        val exception = assertThrows<IllegalStateException> {
            logicContext.execute(chatId, callbackQuery, stepCode)
        }

        assertEquals("Callback not found", exception.message)
    }
}