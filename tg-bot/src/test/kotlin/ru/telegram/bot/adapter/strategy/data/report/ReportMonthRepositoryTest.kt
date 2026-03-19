package ru.telegram.bot.adapter.strategy.data.report

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_0
import ru.telegram.bot.adapter.dto.budget.PeriodReport
import ru.telegram.bot.adapter.strategy.dto.ReportDto.Companion.MONTH

class ReportMonthRepositoryTest: ReportBasicTest() {

    private lateinit var repository: ReportMonthRepository

    @BeforeEach
    fun setup() {
        repository = ReportMonthRepository(reportClient)
    }

    @Test
    fun `getData should return mapped report from client`() {
        val income = formIncomeTxDetail()
        val incomeCategory = income.amountByCategory.keys.first()
        val expense = formExpenseTxDetail()
        val expenseCategory = income.amountByCategory.keys.first()

        val periodReport = PeriodReport(
            balance = income.txTypeAmount.minus(expense.txTypeAmount),
            periodName = MONTH,
            income = income,
            expense = expense
        )

        whenever(reportClient.getThisMonthReport(chatId.toString())).thenReturn(periodReport)

        val result = repository.getData(chatId)

        assertAll(
            { verify(reportClient).getThisMonthReport(chatId.toString()) },
            { assertNotNull(result) },
            { assertEquals(periodReport.balance, result.balance) },
            { assertEquals(periodReport.periodName, result.periodName) },
            { assertEquals(periodReport.income.txTypeAmount, result.income.amount) },
            { assertEquals(periodReport.income.amountByCategory[incomeCategory], result.income.amountByCategory[incomeCategory]) },

            { assertEquals(periodReport.expense.txTypeAmount, result.expense.amount) },
            { assertEquals(periodReport.expense.amountByCategory[expenseCategory], result.expense.amountByCategory[expenseCategory]) },
        )
    }

    @Test
    fun `getData should return stub when client throws exception`() {
        whenever(reportClient.getThisMonthReport(chatId.toString()))
            .thenThrow(RuntimeException("boom"))

        val result = repository.getData(chatId)

        assertAll(
            { verify(reportClient).getThisMonthReport(chatId.toString()) },
            { assertNotNull(result) },
            { assertEquals(AMOUNT_0, result.balance) },
            { assertEquals("default", result.periodName) },
            { assertEquals(AMOUNT_0, result.income.amount) },
            { assertTrue(result.income.amountByCategory.isEmpty()) },
            { assertTrue(result.expense.amountByCategory.isEmpty()) },
        )
    }
}