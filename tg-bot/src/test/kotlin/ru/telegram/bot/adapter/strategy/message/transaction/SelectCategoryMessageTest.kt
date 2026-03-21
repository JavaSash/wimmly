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
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository.Companion.EXPENSE_CATEGORIES_STUB
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository.Companion.INCOME_CATEGORIES_STUB
import ru.telegram.bot.adapter.strategy.dto.SelectCategoryDto
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@ExtendWith(MockitoExtension::class)
class SelectCategoryMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    private lateinit var selectCategoryMessage: SelectCategoryMessage

    @BeforeEach
    fun setup() {
        selectCategoryMessage = SelectCategoryMessage(messageWriter)
    }

    @Test
    fun `message should return correct title for income transaction`() {
        val data = SelectCategoryDto(
            txType = INCOME,
            categories = INCOME_CATEGORIES_STUB
        )

        val result = selectCategoryMessage.message(data)

        assertEquals("💸 <b>Выберите категорию дохода:</b>", result)
    }

    @Test
    fun `message should return correct title for expense transaction`() {
        val data = SelectCategoryDto(
            txType = EXPENSE,
            categories = EXPENSE_CATEGORIES_STUB
        )

        val result = selectCategoryMessage.message(data)

        assertEquals("💸 <b>Выберите категорию расхода:</b>", result)
    }

    @Test
    fun `message should use INCOME as default when data is null`() {
        val result = selectCategoryMessage.message(null)

        assertEquals("💸 <b>Выберите категорию дохода:</b>", result)
    }

    @Test
    fun `inlineButtons should create buttons with correct row positions for 2 columns`() {
        val categories = INCOME_CATEGORIES_STUB
        val data = SelectCategoryDto(txType = INCOME, categories = categories)

        val result = selectCategoryMessage.inlineButtons(CHAT_ID, data)

        assertAll(
            { assertEquals(4, result.size) },
            { assertEquals(0, result[0].rowPos) },
            { assertEquals(categories[0].code, result[0].text) },
            { assertEquals(0, result[1].rowPos) },
            { assertEquals(categories[1].code, result[1].text) },
            { assertEquals(1, result[2].rowPos) },
            { assertEquals(categories[2].code, result[2].text) },
            { assertEquals(1, result[3].rowPos) },
            { assertEquals(categories[3].code, result[3].text) }
        )
    }

    @Test
    fun `inlineButtons should handle odd number of categories`() {
        val categories = INCOME_CATEGORIES_STUB.dropLast(1)
        val data = SelectCategoryDto(txType = INCOME, categories = categories)

        val result = selectCategoryMessage.inlineButtons(CHAT_ID, data)

        assertAll(
            { assertEquals(3, result.size) },
            { assertEquals(0, result[0].rowPos) },
            { assertEquals(categories[0].code, result[0].text) },
            { assertEquals(0, result[1].rowPos) },
            { assertEquals(categories[1].code, result[1].text) },
            { assertEquals(1, result[2].rowPos) },
            { assertEquals(categories[2].code, result[2].text) }
        )
    }

    @Test
    fun `inlineButtons should return empty list when categories is empty`() {
        val data = SelectCategoryDto(txType = INCOME, categories = emptyList())

        val result = selectCategoryMessage.inlineButtons(CHAT_ID, data)

        assertEquals(emptyList<MarkupDataDto>(), result)
    }

    @Test
    fun `inlineButtons should return empty list when data is null`() {
        val result = selectCategoryMessage.inlineButtons(CHAT_ID, null)

        assertEquals(emptyList<MarkupDataDto>(), result)
    }

    @Test
    fun `inlineButtons should handle single category`() {
        val categories = INCOME_CATEGORIES_STUB.dropLast(3)
        val data = SelectCategoryDto(txType = INCOME, categories = categories)

        val result = selectCategoryMessage.inlineButtons(CHAT_ID, data)

        assertAll(
            { assertEquals(1, result.size) },
            { assertEquals(0, result[0].rowPos) },
            { assertEquals(categories[0].code, result[0].text) }
        )
    }
}