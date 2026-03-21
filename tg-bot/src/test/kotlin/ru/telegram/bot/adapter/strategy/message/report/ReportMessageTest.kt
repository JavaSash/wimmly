package ru.telegram.bot.adapter.strategy.message.report

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_0
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_50
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.FORMATTED_MSG
import ru.telegram.bot.adapter.TestConstants.Tx.HOME_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.INVESTMENT_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.dto.view.CategoryInfo
import ru.telegram.bot.adapter.formCategoryInfo
import ru.telegram.bot.adapter.formReportDetail
import ru.telegram.bot.adapter.formReportDto
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.service.ReportService
import ru.telegram.bot.adapter.strategy.dto.ReportDto.Companion.MONTH
import ru.telegram.bot.adapter.strategy.dto.ReportDto.Companion.TODAY
import ru.telegram.bot.adapter.strategy.dto.ReportDto.Companion.WEEK
import ru.telegram.bot.adapter.strategy.dto.ReportDto.Companion.YEAR
import ru.telegram.bot.adapter.utils.formatMoney

@ExtendWith(MockitoExtension::class)
class ReportMessageTest {
    @Mock
    lateinit var messageWriter: MessageWriter

    @Mock
    lateinit var reportService: ReportService

    private lateinit var reportMessage: ReportMessage

    private val periods = listOf(
        TODAY to StepCode.REPORT_TODAY,
        WEEK to StepCode.REPORT_WEEK,
        MONTH to StepCode.REPORT_MONTH,
        YEAR to StepCode.REPORT_YEAR
    )

    @BeforeEach
    fun setup() {
        reportMessage = ReportMessage(messageWriter, reportService)
    }

    @Test
    fun `message should map fields, call reportService and writer`() {
        val dto = formReportDto()
        val incomeMap = dto.income.amountByCategory
        val expenseMap = dto.expense.amountByCategory

        val preparedIncome: List<CategoryInfo> = listOf(
            formCategoryInfo(categoryName = SALARY_CATEGORY, amount = AMOUNT_100),
            formCategoryInfo(categoryName = INVESTMENT_CATEGORY, amount = AMOUNT_100)
        )
        val preparedExpense: List<CategoryInfo> = listOf(
            formCategoryInfo(categoryName = FOOD_CATEGORY, amount = AMOUNT_50),
            formCategoryInfo(categoryName = HOME_CATEGORY, amount = AMOUNT_50)
        )

        whenever(reportService.prepareCategories(incomeMap, dto.income.amount, false)).thenReturn(preparedIncome)
        whenever(reportService.prepareCategories(expenseMap, dto.expense.amount, true)).thenReturn(preparedExpense)
        whenever(messageWriter.process(eq(StepCode.REPORT_TODAY), any())).thenReturn(FORMATTED_MSG)

        val result = reportMessage.message(dto)

        val captor = argumentCaptor<Map<String, Any?>>()

        assertAll(
            { verify(reportService).prepareCategories(incomeMap, dto.income.amount, false) },
            { verify(reportService).prepareCategories(expenseMap, dto.expense.amount, true) },
            { verify(messageWriter).process(eq(StepCode.REPORT_TODAY), captor.capture()) },
            { assertEquals(dto.balance.formatMoney(), captor.firstValue["balance"]) },
            { assertEquals(dto.income.amount.formatMoney(), captor.firstValue["income"]) },
            { assertEquals(dto.expense.amount.formatMoney(), captor.firstValue["expense"]) },
            { assertTrue(captor.firstValue["hasIncomeCategories"] as Boolean) },
            { assertEquals(preparedIncome, captor.firstValue["incomeCategories"]) },
            { assertTrue(captor.firstValue["hasExpenseCategories"] as Boolean) },
            { assertEquals(preparedExpense, captor.firstValue["expenseCategories"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should handle empty categories`() {
        val dto = formReportDto().copy(
            income = formReportDetail(amountByCategory = emptyMap()),
            expense = formReportDetail(amountByCategory = emptyMap())
        )

        whenever(reportService.prepareCategories(emptyMap(), AMOUNT_0, false)).thenReturn(emptyList())
        whenever(reportService.prepareCategories(emptyMap(), AMOUNT_0, true)).thenReturn(emptyList())
        whenever(messageWriter.process(any(), any())).thenReturn(FORMATTED_MSG)

        val result = reportMessage.message(dto)

        val captor = argumentCaptor<Map<String, Any?>>()
        verify(messageWriter).process(any(), captor.capture())
        val capturedTemplateData = captor.firstValue
        assertAll(
            { assertFalse(capturedTemplateData["hasIncomeCategories"] as Boolean) },
            { assertFalse(capturedTemplateData["hasExpenseCategories"] as Boolean) },
            { assertEquals(emptyList<CategoryInfo>(), capturedTemplateData["incomeCategories"]) },
            { assertEquals(emptyList<CategoryInfo>(), capturedTemplateData["expenseCategories"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should handle null data`() {
        whenever(messageWriter.process(eq(StepCode.REPORT_TODAY), any())).thenReturn(FORMATTED_MSG)
        whenever(reportService.prepareCategories(emptyMap(), AMOUNT_0, false)).thenReturn(emptyList())
        whenever(reportService.prepareCategories(emptyMap(), AMOUNT_0, true)).thenReturn(emptyList())

        val result = reportMessage.message(null)

        val captor = argumentCaptor<Map<String, Any?>>()
        verify(messageWriter).process(eq(StepCode.REPORT_TODAY), captor.capture())
        val capturedTemplateData = captor.firstValue

        assertAll(
            { verify(reportService).prepareCategories(emptyMap(), AMOUNT_0, false) },
            { verify(reportService).prepareCategories(emptyMap(), AMOUNT_0, true) },
            { assertNull(capturedTemplateData["balance"]) },
            { assertNull(capturedTemplateData["income"]) },
            { assertNull(capturedTemplateData["expense"]) },
            { assertFalse(capturedTemplateData["hasIncomeCategories"] as Boolean) },
            { assertEquals(emptyList<CategoryInfo>(), capturedTemplateData["incomeCategories"]) },
            { assertFalse(capturedTemplateData["hasExpenseCategories"] as Boolean) },
            { assertEquals(emptyList<CategoryInfo>(), capturedTemplateData["expenseCategories"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }

    @Test
    fun `message should map period correctly for all period types`() {
        periods.forEach { (periodName, expectedStepCode) ->
            val dto = formReportDto().copy(periodName = periodName)

            whenever(reportService.prepareCategories(any(), any(), any())).thenReturn(emptyList())
            whenever(messageWriter.process(any(), any())).thenReturn(FORMATTED_MSG)

            reportMessage.message(dto)

            verify(messageWriter).process(eq(expectedStepCode), any())
        }
    }

    @Test
    fun `message should use REPORT_TODAY as default for unknown period`() {
        val dto = formReportDto().copy(periodName = "UNKNOWN_PERIOD")

        whenever(reportService.prepareCategories(any(), any(), any())).thenReturn(emptyList())
        whenever(messageWriter.process(any(), any())).thenReturn(FORMATTED_MSG)

        reportMessage.message(dto)

        verify(messageWriter).process(eq(StepCode.REPORT_TODAY), any())
    }


    @Test
    fun `message should handle categories with zero amounts`() {
        val dto = formReportDto().copy(
            income = formReportDetail(
                amountByCategory = mapOf(
                    SALARY_CATEGORY to AMOUNT_0,
                    INVESTMENT_CATEGORY to AMOUNT_0
                )
            ),
            expense = formReportDetail(
                amountByCategory = mapOf(
                    FOOD_CATEGORY to AMOUNT_0,
                    HOME_CATEGORY to AMOUNT_0
                )
            )
        )

        val preparedIncome = listOf(
            formCategoryInfo(categoryName = SALARY_CATEGORY, amount = AMOUNT_0),
            formCategoryInfo(categoryName = INVESTMENT_CATEGORY, amount = AMOUNT_0)
        )
        val preparedExpense = listOf(
            formCategoryInfo(categoryName = FOOD_CATEGORY, amount = AMOUNT_0, isExpense = true),
            formCategoryInfo(categoryName = HOME_CATEGORY, amount = AMOUNT_0, isExpense = true)
        )

        whenever(reportService.prepareCategories(dto.income.amountByCategory, AMOUNT_0, false)).thenReturn(
            preparedIncome
        )
        whenever(reportService.prepareCategories(dto.expense.amountByCategory, AMOUNT_0, true)).thenReturn(
            preparedExpense
        )
        whenever(messageWriter.process(any(), any())).thenReturn(FORMATTED_MSG)

        val result = reportMessage.message(dto)

        val captor = argumentCaptor<Map<String, Any?>>()
        verify(messageWriter).process(eq(StepCode.REPORT_TODAY), captor.capture())
        val capturedTemplateData = captor.firstValue

        assertAll(
            { assertEquals(AMOUNT_0.formatMoney(), capturedTemplateData["income"]) },
            { assertEquals(AMOUNT_0.formatMoney(), capturedTemplateData["expense"]) },
            { assertTrue(capturedTemplateData["hasIncomeCategories"] as Boolean) },
            { assertEquals(preparedIncome, capturedTemplateData["incomeCategories"]) },
            { assertTrue(capturedTemplateData["hasExpenseCategories"] as Boolean) },
            { assertEquals(preparedExpense, capturedTemplateData["expenseCategories"]) },
            { assertEquals(FORMATTED_MSG, result) }
        )
    }
}