package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.formCallbackQuery
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@ExtendWith(MockitoExtension::class)
class AskTransactionTypeChooserTest {
    @Mock
    lateinit var searchContextRepository: SearchContextRepository

    private lateinit var chooser: AskTransactionTypeChooser

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        chooser = AskTransactionTypeChooser(searchContextRepository)
    }

    @Test
    fun `execute should set accept true and return FINAL for INCOME`() {
        val callback = formCallbackQuery(data = INCOME)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(searchContextRepository).updateTransactionType(chatId, INCOME) }
        )
    }

    @Test
    fun `execute should set accept false and return FINAL for EXPENSE`() {
        val callback = formCallbackQuery(data = EXPENSE)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(searchContextRepository).updateTransactionType(chatId, EXPENSE) }
        )
    }

    @Test
    fun `execute should do nothing for unknown callback`() {
        val callback = formCallbackQuery(data = "UNKNOWN")

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.NOTHING, result) },
            { verify(searchContextRepository, never()).updateTransactionType(any(), any()) }
        )
    }
}