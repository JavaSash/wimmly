package ru.telegram.bot.adapter.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_50
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository
import ru.telegram.bot.adapter.utils.formatMoney
import java.math.BigDecimal
import java.math.RoundingMode

class ReportServiceTest {
    private val reportService = ReportService()

    @Test
    fun `prepareCategories with income stub returns correctly formatted categories`() {
        val categories = SelectCategoryRepository.INCOME_CATEGORIES_STUB.associate { it.code to AMOUNT_100 }
        val totalSum = categories.values.reduce(BigDecimal::add)

        val result = reportService.prepareCategories(categories, totalSum)
        assertAll(
            { assertEquals(SelectCategoryRepository.INCOME_CATEGORIES_STUB.size, result.size) },
            { assertTrue(result.all { it.formattedAmount == AMOUNT_100.formatMoney() }) },
            { assertEquals(totalSum, result.map { it.amount }.reduce(BigDecimal::add)) }, // общая сумма в ответе считается верно
            {// проверяем, что сумма процентов 100%
                val sumPercentages = result.map {
                    it.percentage.removeSuffix("%").toBigDecimal()
                }.reduce(BigDecimal::add)
                assertEquals(BigDecimal(100).setScale(1), sumPercentages.setScale(1))},
            { assertFalse(result.all { it.isExpense }) }
        )
    }

    @Test
    fun `prepareCategories with expense stub calculates percentages correctly`() {
        val categories = SelectCategoryRepository.EXPENSE_CATEGORIES_STUB.take(3)
            .associate { it.code to BigDecimal(it.code.length * 10) }
        val total = categories.values.reduce(BigDecimal::add)

        val result = reportService.prepareCategories(categories, total, isExpense = true)

        assertAll(
            { assertEquals(3, result.size) },
            { assertTrue(result.all { it.isExpense }) },
            {
                // Правильная проверка суммы процентов
                val sumPercentages = result.map { it.percentage.removeSuffix("%").toBigDecimal() }
                    .fold(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(0, RoundingMode.HALF_UP)
                assertEquals(BigDecimal(100), sumPercentages)
            }
        )
    }

    @Test
    fun `prepareCategories cleans category name correctly`() {
        val input = mapOf(
            "category(description=Food), extra" to AMOUNT_50
        )

        val result = reportService.prepareCategories(input, AMOUNT_50)

        assertEquals("Food", result.first().name)
    }

    @Test
    fun `prepareCategories returns empty list for empty input`() {
        val result = reportService.prepareCategories(emptyMap(), AMOUNT_100)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `prepareCategories handles totalAmount zero`() {
        val input = mapOf(
            SelectCategoryRepository.INCOME_CATEGORIES_STUB.first().code to AMOUNT_50
        )
        val result = reportService.prepareCategories(input, BigDecimal.ZERO)
        assertEquals("0%", result.first().percentage)
    }


}