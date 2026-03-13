package ru.wimme.logic.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import ru.wimme.logic.BasicTest
import ru.wimme.logic.TestConstants.Tx.AMOUNT_0
import ru.wimme.logic.TestConstants.Tx.AMOUNT_100
import ru.wimme.logic.TestConstants.Tx.AMOUNT_250
import ru.wimme.logic.TestConstants.Tx.AMOUNT_50
import ru.wimme.logic.TestConstants.User.USER_ID_2
import ru.wimme.logic.model.transaction.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class BalanceServiceTest : BasicTest() {

    @Autowired
    lateinit var balanceService: BalanceService

    private val monthStart = LocalDate
        .now()
        .withDayOfMonth(1)
        .atStartOfDay()

    private val now: LocalDateTime = monthStart.plusHours(1)

    @Test
    fun `user with no transactions should return zero balance`() {
        val user = initUser()

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(BigDecimal.ZERO, balance.income) },
            { assertEquals(BigDecimal.ZERO, balance.expense) },
            { assertEquals(BigDecimal.ZERO, balance.balance) },
        )
    }


    @Test
    fun `only income transactions`() {
        val user = initUser()
        val tx1 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_100,
            createdAt = now.plusMinutes(1)
        )
        val tx2 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_250,
            createdAt = now.plusMinutes(1)
        )
        val sum = (tx1.amount + tx2.amount).setScale(2)

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(sum, balance.income) },
            { assertEquals(BigDecimal.ZERO, balance.expense) },
            { assertEquals(sum, balance.balance) },
        )
    }


    @Test
    fun `only expense transactions`() {
        val user = initUser()
        val tx1 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            amount = AMOUNT_100,
            createdAt = now.plusMinutes(1)
        )
        val tx2 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            amount = AMOUNT_250,
            createdAt = now.plusMinutes(1)
        )
        val sum = (tx1.amount + tx2.amount).setScale(2)

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(BigDecimal.ZERO, balance.income) },
            { assertEquals(sum, balance.expense) },
            { assertEquals(-sum, balance.balance) },
        )
    }


    @Test
    fun `mixed income and expense`() {
        val user = initUser()
        val tx1 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_250,
            createdAt = now.plusMinutes(1)
        )
        val tx2 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            amount = AMOUNT_100,
            createdAt = now.plusMinutes(1)
        )
        val tx3 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_50,
            createdAt = now.plusMinutes(1)
        )

        val income = (tx1.amount + tx3.amount).setScale(2)

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(income, balance.income) },
            { assertEquals(tx2.amount.setScale(2), balance.expense) },
            { assertEquals(income - tx2.amount, balance.balance) },
        )
    }


    @Test
    fun `zero amount transactions`() {
        val user = initUser()
        val tx1 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_0,
            createdAt = now.plusMinutes(1)
        )
        val tx2 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            amount = AMOUNT_0,
            createdAt = now.plusMinutes(1)
        )

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(0, tx1.amount.compareTo(balance.income)) },
            { assertEquals(0, tx2.amount.compareTo(balance.expense)) },
            { assertEquals(0, tx1.amount.subtract(tx2.amount).compareTo(balance.balance)) },
        )
    }

    @Test
    fun `balance calculates only for this user`() {
        val user1 = initUser()
        val user2 = initUser(userId = USER_ID_2)
        val tx1 = initTransactionWithCreatedAt(
            userId = user1.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_100,
            createdAt = now.plusMinutes(1)
        )
        initTransactionWithCreatedAt(
            userId = user2.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_250,
            createdAt = now.plusMinutes(1)
        )

        val balance = balanceService.getBalance(user1.tgId)

        assertAll(
            { assertEquals(tx1.amount.setScale(2), balance.income) },
            { assertEquals(BigDecimal.ZERO, balance.expense) },
            { assertEquals(tx1.amount.setScale(2), balance.balance) },
        )
    }


    @Test
    fun `negative amounts still sum as-is`() {
        val user = initUser()
        val tx1 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = -AMOUNT_100,
            createdAt = now.plusMinutes(1)
        )
        val tx2 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            amount = -AMOUNT_50,
            createdAt = now.plusMinutes(1)
        )

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(tx1.amount.setScale(2), balance.income) },
            { assertEquals(tx2.amount.setScale(2), balance.expense) },
            { assertEquals((tx1.amount - tx2.amount).setScale(2), balance.balance) },
        )
    }

    @Test
    fun `balance includes old transactions but income counts only this month`() {
        val user = initUser()

        val prevMonth = monthStart.minusDays(5)

        val tx1 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_100,
            createdAt = prevMonth
        )

        val tx2 = initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            amount = AMOUNT_250,
            createdAt = now
        )

        val sum = (tx1.amount + tx2.amount).setScale(2)

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(tx2.amount.setScale(2), balance.income) },
            { assertEquals(BigDecimal.ZERO, balance.expense) },
            { assertEquals(sum, balance.balance) }
        )
    }
}
