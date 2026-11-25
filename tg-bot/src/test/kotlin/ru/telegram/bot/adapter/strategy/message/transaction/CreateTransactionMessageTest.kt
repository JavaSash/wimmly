package ru.telegram.bot.adapter.strategy.message.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullAndEmptySource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.config.FreemarkerConfig
import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.formTransactionRs
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME
import java.math.BigDecimal
import java.time.Instant

class CreateTransactionMessageTest {
    private val categories = mock<SelectCategoryRepository>()
    private val writer = MessageWriter(FreemarkerConfig().freeMarkerConfigurer().apply { afterPropertiesSet() })
    private val message = CreateTransactionMessage(writer, categories)

    @Test
    fun `render should show saved expense data and escape the comment`() {
        val transaction = formTransactionRs(type = EXPENSE, category = "FOOD").copy(
            displayId = 1234,
            amount = BigDecimal("1500.50"),
            createdAt = Instant.parse("2026-11-25T12:00:00Z"),
            comment = "Покупки <магазин> & доставка",
        )
        whenever(categories.getCategories(EXPENSE)).thenReturn(listOf(CategoryDto("FOOD", "Еда", EXPENSE)))

        assertEquals(
            """
            ✅ <b>Транзакция успешно добавлена!</b>

            Номер: #1234
            Тип: Расход
            Категория: Еда
            Сумма: 1500.50 ₽
            Дата: 25.11.2026
            Комментарий: Покупки &lt;магазин&gt; &amp; доставка
            """.trimIndent(),
            message.render(transaction).trim()
        )
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = ["   "])
    fun `render should omit missing or blank comments for income`(comment: String?) {
        val transaction = formTransactionRs(type = INCOME, category = "SALARY").copy(comment = comment)
        whenever(categories.getCategories(INCOME)).thenReturn(listOf(CategoryDto("SALARY", "Зарплата", INCOME)))

        val result = message.render(transaction)

        assertTrue("Тип: Доход" in result)
        assertTrue("Категория: Зарплата" in result)
        assertFalse("Комментарий:" in result)
    }
}
