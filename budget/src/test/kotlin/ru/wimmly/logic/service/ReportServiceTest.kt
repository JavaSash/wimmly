package ru.wimmly.logic.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import ru.wimmly.logic.BasicTest
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_0
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_100
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_250
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_50
import ru.wimmly.logic.TestConstants.User.USER_ID
import ru.wimmly.logic.TestConstants.User.USER_ID_2
import ru.wimmly.logic.TestConstants.User.USER_NAME
import ru.wimmly.logic.TestConstants.User.USER_NAME_2
import ru.wimmly.logic.model.entity.TransactionEntity
import ru.wimmly.logic.model.transaction.TransactionType
import java.math.BigDecimal

class ReportServiceTest : BasicTest() {

    @Autowired
    lateinit var reportService: ReportService
    @Autowired
    lateinit var txService: TransactionService

    lateinit var tx1: TransactionEntity
    lateinit var tx2: TransactionEntity
    lateinit var tx3: TransactionEntity
    lateinit var incomeAmount: BigDecimal
    lateinit var expenseAmount: BigDecimal

    @BeforeEach
    fun setupUsersAndTransactions() {
        initUser(userId = USER_ID, name = USER_NAME)
        initUser(userId = USER_ID_2, name = USER_NAME_2)
        tx1 = initTransaction(
            userId = USER_ID,
            type = TransactionType.INCOME,
            amount = AMOUNT_100
        )
        tx2 = initTransaction(
            userId = USER_ID,
            type = TransactionType.EXPENSE,
            amount = AMOUNT_50
        )
        tx3 = initTransaction(
            userId = USER_ID,
            type = TransactionType.INCOME,
            amount = AMOUNT_250
        )

        incomeAmount = tx1.amount + tx3.amount
        expenseAmount = tx2.amount
    }

    @Test
    fun `formTodayReport calculates totals correctly`() {
        val report = reportService.formTodayReport(USER_ID)
        assertAll(
            { assertEquals("Сегодня", report.periodName) },
            { assertEquals(incomeAmount, report.totalIncome) },
            { assertEquals(expenseAmount, report.totalExpense) }
        )
    }

    @Test
    fun `formThisWeekReport calculates totals correctly`() {
        val report = reportService.formThisWeekReport(USER_ID)
        assertAll(
            { assertEquals("Эта неделя", report.periodName) },
            { assertEquals(incomeAmount, report.totalIncome) },
            { assertEquals(expenseAmount, report.totalExpense) })
    }

    @Test
    fun `formThisMonthReport calculates totals correctly`() {
        val report = reportService.formThisMonthReport(USER_ID)
        assertAll(
            { assertEquals("Этот месяц", report.periodName) },
            { assertEquals(incomeAmount, report.totalIncome) },
            { assertEquals(expenseAmount, report.totalExpense) })
    }

    @Test
    fun `formThisYearReport calculates totals correctly`() {
        val report = reportService.formThisYearReport(USER_ID)
        assertAll(
            { assertEquals("Этот год", report.periodName) },
            { assertEquals(incomeAmount, report.totalIncome) },
            { assertEquals(expenseAmount, report.totalExpense) })
    }

    @Test
    fun `reportForPeriod returns empty totals for user with no transactions`() {
        val report = reportService.formTodayReport(USER_ID_2)
        assertAll(
            { assertEquals("Сегодня", report.periodName) },
            { assertEquals(BigDecimal.ZERO, report.totalIncome) },
            { assertEquals(BigDecimal.ZERO, report.totalExpense) },
            { assertTrue(report.details.isEmpty()) }
        )
    }

    @Test
    fun `reportForPeriod handles transactions with zero amount`() {
        val tx4 = initTransaction(userId = USER_ID, type = TransactionType.EXPENSE, amount = AMOUNT_0)
        val report = reportService.formTodayReport(USER_ID)
        assertAll(
            { assertEquals(incomeAmount, report.totalIncome) },
            { assertEquals(expenseAmount + tx4.amount, report.totalExpense) }
        )
    }

    @Test
    fun `reportForPeriod ignores transactions outside period`() {
        //todo impl
    }

    @Test
    fun `reportForPeriod sorts details by descending total`() {
        val report = reportService.formTodayReport(USER_ID)
        val sorted = report.details.sortedByDescending { it.total }
        assertEquals(sorted, report.details)
    }

}