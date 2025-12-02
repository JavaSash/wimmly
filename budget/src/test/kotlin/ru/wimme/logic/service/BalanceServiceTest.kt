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

class BalanceServiceTest : BasicTest() {

    @Autowired
    lateinit var balanceService: BalanceService

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
        val tx1 = initTransaction(user.tgId, TransactionType.INCOME, AMOUNT_100)
        val tx2 = initTransaction(user.tgId, TransactionType.INCOME, AMOUNT_250)
        val sum = tx1.amount + tx2.amount

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
        val tx1 = initTransaction(user.tgId, TransactionType.EXPENSE, AMOUNT_100)
        val tx2 = initTransaction(user.tgId, TransactionType.EXPENSE, AMOUNT_250)
        val sum = tx1.amount + tx2.amount

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
        val tx1 = initTransaction(user.tgId, TransactionType.INCOME, AMOUNT_250)
        val tx2 = initTransaction(user.tgId, TransactionType.EXPENSE, AMOUNT_100)
        val tx3 = initTransaction(user.tgId, TransactionType.INCOME, AMOUNT_50)
        val income = tx1.amount + tx3.amount

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            { assertEquals(income, balance.income) },
            { assertEquals(tx2.amount, balance.expense) },
            { assertEquals(income - tx2.amount, balance.balance) },
        )
    }


    @Test
    fun `zero amount transactions`() {
        val user = initUser()
        val tx1 = initTransaction(userId = user.tgId, type = TransactionType.INCOME, amount = AMOUNT_0)
        val tx2 = initTransaction(userId = user.tgId, type = TransactionType.EXPENSE, amount = AMOUNT_0)

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
        val tx1 = initTransaction(user1.tgId, TransactionType.INCOME, AMOUNT_100)
        initTransaction(user2.tgId, TransactionType.INCOME, AMOUNT_250)

        val balance = balanceService.getBalance(user1.tgId)

        assertAll(
            {assertEquals(tx1.amount, balance.income)},
            {assertEquals(BigDecimal.ZERO, balance.expense)},
            {assertEquals(tx1.amount, balance.balance)},
        )
    }


    @Test
    fun `negative amounts still sum as-is`() {
        val user = initUser()
        val tx1 = initTransaction(user.tgId, TransactionType.INCOME, -AMOUNT_100)
        val tx2 = initTransaction(user.tgId, TransactionType.EXPENSE, -AMOUNT_50)

        val balance = balanceService.getBalance(user.tgId)

        assertAll(
            {assertEquals(tx1.amount, balance.income)},
            {assertEquals(tx2.amount, balance.expense)},
            {assertEquals(tx1.amount - tx2.amount, balance.balance)},
        )
    }
}
