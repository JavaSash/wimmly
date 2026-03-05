package ru.wimme.logic.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import ru.wimme.logic.BasicTest
import ru.wimme.logic.TestConstants.Tx.AMOUNT_0
import ru.wimme.logic.TestConstants.Tx.AMOUNT_100
import ru.wimme.logic.TestConstants.Tx.AMOUNT_150
import ru.wimme.logic.TestConstants.Tx.AMOUNT_250
import ru.wimme.logic.TestConstants.Tx.AMOUNT_50
import ru.wimme.logic.TestConstants.User.USER_ID
import ru.wimme.logic.TestConstants.User.USER_ID_2
import ru.wimme.logic.exception.NotFoundException
import ru.wimme.logic.model.transaction.ExpenseCategory
import ru.wimme.logic.model.transaction.TransactionRq
import ru.wimme.logic.model.transaction.TransactionSearchRq
import ru.wimme.logic.model.transaction.TransactionType
import ru.wimme.logic.money
import java.time.LocalDateTime
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
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_100.money(),
            comment = "Test create",
            date = null
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
            category = ExpenseCategory.FOOD.name,
            amount = AMOUNT_250.money(),
            comment = "Updated",
            date = null
        )

        val updated = txService.update(tx.id!!, rq)

        assertAll(
            { assertEquals(tx.id, updated.id) },
            { assertEquals(TransactionType.EXPENSE, updated.type) },
            { assertEquals(ExpenseCategory.FOOD.name, updated.category) },
            { assertEquals(AMOUNT_250.money(), updated.amount) },
            { assertEquals("Updated", updated.comment) }
        )
    }

    @Test
    fun `update - should throw NotFoundException when not exists`() {
        val rq = TransactionRq(
            type = TransactionType.EXPENSE,
            userId = USER_ID,
            category = ExpenseCategory.FOOD.name,
            amount = AMOUNT_100.money(),
            comment = "X",
            date = null
        )

        assertThrows<NotFoundException> {
            txService.update(UUID.randomUUID(), rq)
        }
    }

    @Test
    fun `create - should assign sequential display_id for new user`() {
        val user = initUser()

        val tx1 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "First",
                date = null
            )
        )

        val tx2 = txService.create(
            TransactionRq(
                type = TransactionType.EXPENSE,
                userId = user.tgId,
                category = ExpenseCategory.FOOD.name,
                amount = AMOUNT_50.money(),
                comment = "Second",
                date = null
            )
        )

        val tx3 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.FOOD.name,
                amount = AMOUNT_250.money(),
                comment = "Third",
                date = null
            )
        )

        assertAll(
            { assertEquals(1L, tx1.displayId) },
            { assertEquals(2L, tx2.displayId) },
            { assertEquals(3L, tx3.displayId) }
        )
    }

    @Test
    fun `create - should increment display_id independently for different users`() {
        val user1 = initUser(USER_ID)
        val user2 = initUser(USER_ID_2)

        val user1Tx1 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user1.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = null,
                date = null
            )
        )

        val user2Tx1 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user2.tgId,
                category = ExpenseCategory.FOOD.name,
                amount = AMOUNT_250.money(),
                comment = null,
                date = null
            )
        )

        val user1Tx2 = txService.create(
            TransactionRq(
                type = TransactionType.EXPENSE,
                userId = user1.tgId,
                category = ExpenseCategory.FOOD.name,
                amount = AMOUNT_50.money(),
                comment = null,
                date = null
            )
        )

        assertAll(
            { assertEquals(1L, user1Tx1.displayId) },
            { assertEquals(1L, user2Tx1.displayId) },
            { assertEquals(2L, user1Tx2.displayId) }
        )
    }

    @Test
    fun `create - should continue sequence after deletion (delete middle trx)`() {
        val user = initUser()

        // Создаем 4 транзакции: display_id 1,2,3,4
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=1",
                date = null
            )
        )
        val tx2 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=2",
                date = null
            )
        )
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=3",
                date = null
            )
        )
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=4",
                date = null
            )
        )

        // Удаляем транзакцию с display_id = 2
        txService.delete(tx2.id!!)

        // Добавляем новую транзакцию
        val newTx = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "New after delete",
                date = null
            )
        )

        val allTxs = txService.getUserTransactions(user.tgId).sortedBy { it.displayId }

        assertAll(
            { assertEquals(4, allTxs.size) },
            { assertEquals(1L, allTxs[0].displayId) },
            { assertEquals(3L, allTxs[1].displayId) },
            { assertEquals(4L, allTxs[2].displayId) },
            { assertEquals(5L, allTxs[3].displayId) },
            { assertEquals(5L, newTx.displayId) }
        )
    }

    @Test
    fun `create - should continue sequence after deletion (delete last trx)`() {
        val user = initUser()

        // Создаем 4 транзакции: display_id 1,2,3,4
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=1",
                date = null
            )
        )
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=2",
                date = null
            )
        )
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=3",
                date = null
            )
        )
        val tx4 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=4",
                date = null
            )
        )

        // Удаляем последнюю транзакцию (display_id = 4)
        txService.delete(tx4.id!!)

        // Добавляем новую транзакцию
        val newTx = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "New after delete last",
                date = null
            )
        )

        val allTxs = txService.getUserTransactions(user.tgId).sortedBy { it.displayId }

        assertAll(
            { assertEquals(4, allTxs.size) },
            { assertEquals(1L, allTxs[0].displayId) },
            { assertEquals(2L, allTxs[1].displayId) },
            { assertEquals(3L, allTxs[2].displayId) },
            { assertEquals(5L, allTxs[3].displayId) },
            { assertEquals(5L, newTx.displayId) }
        )
    }

    @Test
    fun `create - should handle sequence when user has no transactions`() {
        val user = initUser()

        // У пользователя нет транзакций и нет записи в user_display_id_seq
        // При создании первой транзакции должна создаться запись в seq таблице
        val tx = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "First tx",
                date = null
            )
        )

        val seq = userDisplayIdSeqRepository.findByIdOrNull(user.tgId)

        assertAll(
            { assertEquals(1L, tx.displayId) },
            { assertNotNull(seq) },
            { assertEquals(1L, seq?.currentSeq) }
        )
    }

    @Test
    fun `delete - should not affect display_id sequence for other users`() {
        val user1 = initUser(USER_ID)
        val user2 = initUser(USER_ID_2)

        // User1 создает транзакции 1,2,3
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user1.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=1",
                date = null
            )
        )
        val u1tx2 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user1.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=2",
                date = null
            )
        )
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user1.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=3",
                date = null
            )
        )

        // User2 создает транзакции 1,2
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user2.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=1",
                date = null
            )
        )
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user2.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=2",
                date = null
            )
        )

        // User1 удаляет свою транзакцию
        txService.delete(u1tx2.id!!)
        // User1 добавляет новую транзакцию
        txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user1.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "displayId=4",
                date = null
            )
        )

        // User2 добавляет новую
        val u2tx3 = txService.create(
            TransactionRq(
                type = TransactionType.INCOME,
                userId = user2.tgId,
                category = ExpenseCategory.EDUCATION.name,
                amount = AMOUNT_100.money(),
                comment = "New for user2",
                date = null
            )
        )

        val user1Txs = txService.getUserTransactions(user1.tgId).sortedBy { it.displayId }
        val user2Txs = txService.getUserTransactions(user2.tgId).sortedBy { it.displayId }

        assertAll(
            // User1: остались 1,3 + добавили новую 4
            { assertEquals(3, user1Txs.size) },
            { assertEquals(1L, user1Txs[0].displayId) },
            { assertEquals(3L, user1Txs[1].displayId) },
            { assertEquals(4L, user1Txs[2].displayId) },

            // User2: были 1,2 + новая 3
            { assertEquals(3, user2Txs.size) },
            { assertEquals(1L, user2Txs[0].displayId) },
            { assertEquals(2L, user2Txs[1].displayId) },
            { assertEquals(3L, user2Txs[2].displayId) },
            { assertEquals(3L, u2tx3.displayId) }
        )
    }

    @Test
    fun `update - should not change display_id`() {
        val user = initUser()
        val tx = initTransaction(userId = user.tgId, displayId = 7L, amount = AMOUNT_50)

        val rq = TransactionRq(
            type = TransactionType.EXPENSE,
            userId = user.tgId,
            category = ExpenseCategory.FOOD.name,
            amount = AMOUNT_250.money(),
            comment = "Updated",
            date = null
        )

        val updated = txService.update(tx.id!!, rq)

        assertAll(
            { assertEquals(tx.id, updated.id) },
            { assertEquals(7L, updated.displayId) }, // display_id не изменился
            { assertEquals(TransactionType.EXPENSE, updated.type) },
            { assertEquals(ExpenseCategory.FOOD.name, updated.category) },
            { assertEquals(AMOUNT_250.money(), updated.amount) },
            { assertEquals("Updated", updated.comment) }
        )
    }

    @Test
    fun `findTransactionsWithFilters - should return filtered transactions with limit`() {
        val user = initUser()
        val user2 = initUser(userId = "1")

        // another user
        initTransaction(
            userId = user2.tgId,
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_100,
            displayId = 1L
        )
        initTransaction(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_100,
            displayId = 1L
        )
        val tx2 = initTransaction(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_150,
            displayId = 2L
        )
        // other category
        initTransaction(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.FOOD.name,
            amount = AMOUNT_50,
            displayId = 3L
        )
        val tx4 = initTransaction(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_250,
            displayId = 4L
        )
        val tx5 = initTransaction(
            userId = user.tgId,
            type = TransactionType.EXPENSE,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_0,
            displayId = 5L
        )
        // another type
        initTransaction(
            userId = user.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_0,
            displayId = 5L
        )

        val rq = TransactionSearchRq(
            type = TransactionType.EXPENSE,
            userId = user.tgId,
            category = ExpenseCategory.EDUCATION.name,
            limit = 3
        )

        val result = txService.findTransactionsWithFilters(rq)

        assertAll(
            { assertEquals(3, result.size) },
            { assertEquals(tx5.amount, result[0].amount) },
            { assertEquals(tx5.displayId, result[0].displayId) },
            { assertEquals(tx4.amount, result[1].amount) },
            { assertEquals(tx4.displayId, result[1].displayId) },
            { assertEquals(tx2.amount, result[2].amount) },
            { assertEquals(tx2.displayId, result[2].displayId) }
        )
    }

    @Test
    fun `findTransactionsWithFilters - should return empty list when no matches`() {
        val user = initUser()

        initTransaction(
            userId = user.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_100
        )

        val rq = TransactionSearchRq(
            type = TransactionType.EXPENSE,
            userId = user.tgId,
            category = ExpenseCategory.EDUCATION.name,
            limit = 10
        )

        assertTrue(txService.findTransactionsWithFilters(rq).isEmpty())
    }

    @Test
    fun `findTransactionsWithFilters - should return transactions sorted by createdAt desc`() {
        val user = initUser()
        val now = LocalDateTime.now()

        // Создаем транзакции с разными датами
        initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION,
            amount = AMOUNT_100,
            createdAt = now.minusSeconds(3600), // час назад
            displayId = 1L
        )
        initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION,
            amount = AMOUNT_50,
            createdAt = now, // сейчас
            displayId = 2L
        )
        initTransactionWithCreatedAt(
            userId = user.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION,
            amount = AMOUNT_150,
            createdAt = now.minusSeconds(1800), // 30 минут назад
            displayId = 3L
        )

        val rq = TransactionSearchRq(
            type = TransactionType.INCOME,
            userId = user.tgId,
            category = ExpenseCategory.EDUCATION.name,
            limit = 3
        )

        val result = txService.findTransactionsWithFilters(rq)

        assertAll(
            { assertEquals(3, result.size) },
            { assertEquals(AMOUNT_50.money(), result[0].amount) }, // самая новая (tx2)
            { assertEquals(AMOUNT_150.money(), result[1].amount) }, // tx3
            { assertEquals(AMOUNT_100.money(), result[2].amount) }  // tx1
        )
    }

    @Test
    fun `findTransactionsWithFilters - should work for different users independently`() {
        val user1 = initUser(USER_ID)
        val user2 = initUser(USER_ID_2)

        // Транзакции для user1
        initTransaction(
            userId = user1.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_100,
            displayId = 1L
        )
        initTransaction(
            userId = user1.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_150,
            displayId = 2L
        )

        // Транзакции для user2
        initTransaction(
            userId = user2.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_50,
            displayId = 1L
        )
        initTransaction(
            userId = user2.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_250,
            displayId = 2L
        )
        initTransaction(
            userId = user2.tgId,
            type = TransactionType.INCOME,
            category = ExpenseCategory.EDUCATION.name,
            amount = AMOUNT_0,
            displayId = 3L
        )

        val rq1 = TransactionSearchRq(
            type = TransactionType.INCOME,
            userId = user1.tgId,
            category = ExpenseCategory.EDUCATION.name,
            limit = 10
        )

        val rq2 = TransactionSearchRq(
            type = TransactionType.INCOME,
            userId = user2.tgId,
            category = ExpenseCategory.EDUCATION.name,
            limit = 10
        )

        val result1 = txService.findTransactionsWithFilters(rq1)
        val result2 = txService.findTransactionsWithFilters(rq2)

        assertAll(
            { assertEquals(2, result1.size) },
            { assertEquals(3, result2.size) }
        )
    }
}