package ru.telegram.bot.adapter.strategy.data.report

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants
import ru.telegram.bot.adapter.dto.budget.PeriodReport
import ru.telegram.bot.adapter.strategy.dto.ReportDto

class ReportWeekRepositoryTest: ReportBasicTest() {

    private lateinit var repository: ReportWeekRepository

    @BeforeEach
    fun setup() {
        repository = ReportWeekRepository(reportClient)
    }

    @Test
    fun `getData should return mapped report from client`() {
        val income = formIncomeTxDetail()
        val incomeCategory = income.amountByCategory.keys.first()
        val expense = formExpenseTxDetail()
        val expenseCategory = income.amountByCategory.keys.first()

        val periodReport = PeriodReport(
            balance = income.txTypeAmount.minus(expense.txTypeAmount),
            periodName = ReportDto.WEEK,
            income = income,
            expense = expense
        )

        whenever(reportClient.getThisWeekReport(chatId.toString())).thenReturn(periodReport)

        val result = repository.getData(chatId)

        assertAll(
            { verify(reportClient).getThisWeekReport(chatId.toString()) },
            { Assertions.assertNotNull(result) },
            { Assertions.assertEquals(periodReport.balance, result.balance) },
            { Assertions.assertEquals(periodReport.periodName, result.periodName) },
            { Assertions.assertEquals(periodReport.income.txTypeAmount, result.income.amount) },
            { Assertions.assertEquals(periodReport.income.amountByCategory[incomeCategory], result.income.amountByCategory[incomeCategory]) },

            { Assertions.assertEquals(periodReport.expense.txTypeAmount, result.expense.amount) },
            { Assertions.assertEquals(periodReport.expense.amountByCategory[expenseCategory], result.expense.amountByCategory[expenseCategory]) },
        )
    }

    @Test
    fun `getData should return stub when client throws exception`() {
        whenever(reportClient.getThisWeekReport(chatId.toString()))
            .thenThrow(RuntimeException("boom"))

        val result = repository.getData(chatId)

        assertAll(
            { verify(reportClient).getThisWeekReport(chatId.toString()) },
            { Assertions.assertNotNull(result) },
            { Assertions.assertEquals(TestConstants.Tx.AMOUNT_0, result.balance) },
            { Assertions.assertEquals("default", result.periodName) },
            { Assertions.assertEquals(TestConstants.Tx.AMOUNT_0, result.income.amount) },
            { Assertions.assertTrue(result.income.amountByCategory.isEmpty()) },
            { Assertions.assertTrue(result.expense.amountByCategory.isEmpty()) },
        )
    }
}