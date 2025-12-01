package ru.wimmly.logic.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import ru.wimmly.logic.BasicTest
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_100
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_250
import ru.wimmly.logic.TestConstants.Tx.AMOUNT_50
import ru.wimmly.logic.TestConstants.User.USER_ID
import ru.wimmly.logic.TestConstants.User.USER_ID_2
import ru.wimmly.logic.exception.NotFoundException
import ru.wimmly.logic.model.transaction.TransactionCategory
import ru.wimmly.logic.model.transaction.TransactionRq
import ru.wimmly.logic.model.transaction.TransactionType
import ru.wimmly.logic.money
import java.util.*
class TransactionServiceTest : BasicTest() {

    @Autowired
    lateinit var txService: TransactionService

    @Test
    fun `create - should save transaction`() {
        val user = initUser()

        val rq = TransactionRq(
            type = TransactionType.INCOME,
            userId = user.tgId,
            category = TransactionCategory.EDUCATION,
            amount = AMOUNT_100.money(),
            comment = "Test create"
        )

        val tx = txService.create(rq)

        assertAll(
            { assertNotNull(tx.id) },
            { assertEquals(rq.type, tx.type) },
            { assertEquals(user.tgId, tx.userId) },
            { assertEquals(rq.category, tx.category) },
            { assertEquals(rq.amount, tx.amount) },
            { assertEquals(rq.comment, tx.comment) }
        )
    }

    @Test
    fun `getById - should return transaction`() {
        val user = initUser()
        val saved = initTransaction(userId = user.tgId, amount = AMOUNT_250)

        val found = txService.getById(saved.id!!)

        assertAll(
            { assertEquals(saved.id, found.id) },
            { assertEquals(saved.amount, found.amount) }
        )
    }

    @Test
    fun `getById - should throw NotFoundException`() {
        assertThrows<NotFoundException> {
            txService.getById(UUID.randomUUID())
        }
    }

    @Test
    fun `getUserTransactions - should return only user transactions`() {
        val user1 = initUser(USER_ID)
        val user2 = initUser(USER_ID_2)

        val tx1 = initTransaction(userId = user1.tgId, amount = AMOUNT_50)
        initTransaction(userId = user2.tgId, amount = AMOUNT_250)

        val list = txService.getUserTransactions(user1.tgId)

        assertAll(
            { assertEquals(1, list.size) },
            { assertEquals(tx1.id, list[0].id) },
            { assertEquals(tx1.amount, list[0].amount) }
        )
    }

    @Test
    fun `delete - should delete transaction`() {
        val user = initUser()
        val tx = initTransaction(userId = user.tgId)

        txService.delete(tx.id!!)

        assertFalse(txRepo.existsById(tx.id!!))
    }

    @Test
    fun `delete - should throw when not exists`() {
        assertThrows<IllegalArgumentException> {
            txService.delete(UUID.randomUUID())
        }
    }

    @Test
    fun `update - should update fields`() {
        val user = initUser()
        val tx = initTransaction(userId = user.tgId, amount = AMOUNT_50)

        val rq = TransactionRq(
            type = TransactionType.EXPENSE,
            userId = USER_ID,
            category = TransactionCategory.FOOD,
            amount = AMOUNT_250.money(),
            comment = "Updated"
        )

        val updated = txService.update(tx.id!!, rq)

        assertAll(
            { assertEquals(tx.id, updated.id) },
            { assertEquals(TransactionType.EXPENSE, updated.type) },
            { assertEquals(TransactionCategory.FOOD, updated.category) },
            { assertEquals(AMOUNT_250.money(), updated.amount) },
            { assertEquals("Updated", updated.comment) }
        )
    }

    @Test
    fun `update - should throw NotFoundException when not exists`() {
        val rq = TransactionRq(
            type = TransactionType.EXPENSE,
            userId = USER_ID,
            category = TransactionCategory.FOOD,
            amount = AMOUNT_100.money(),
            comment = "X"
        )

        assertThrows<NotFoundException> {
            txService.update(UUID.randomUUID(), rq)
        }
    }
}