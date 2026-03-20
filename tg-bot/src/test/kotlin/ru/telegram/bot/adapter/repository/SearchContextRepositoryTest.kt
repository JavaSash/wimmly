package ru.telegram.bot.adapter.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.telegram.bot.adapter.DbBasicTest
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.TRX_ID
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

class SearchContextRepositoryTest: DbBasicTest() {

    @BeforeEach
    fun setUp() {
        chatCtxRepo.createUser(CHAT_ID)
    }

    @Test
    fun `should create search context`() {
        val ctx = searchCtxRepo.createSearchContext(CHAT_ID)

        assertAll(
            { assertEquals(CHAT_ID, ctx.chatId) },
            { assertNull(ctx.type) },
            { assertNull(ctx.category) },
            { assertNull(ctx.trxId) },
        )
    }

    @Test
    fun `findById should return null when context does not exist`() {
        assertNull(searchCtxRepo.findById(999))
    }

    @Test
    fun `findById should return context`() {
        searchCtxRepo.createSearchContext(CHAT_ID)

        val ctx = searchCtxRepo.findById(CHAT_ID)

        assertAll(
            { assertNotNull(ctx) },
            { assertEquals(CHAT_ID, ctx!!.chatId) }
        )
    }

    @Test
    fun `should update transaction type`() {
        searchCtxRepo.createSearchContext(CHAT_ID)

        searchCtxRepo.updateTransactionType(CHAT_ID, INCOME)

        val ctx = searchCtxRepo.findById(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, ctx.chatId) },
            { assertEquals(INCOME, ctx.type) }
        )
    }

    @Test
    fun `should update category`() {
        val category = SALARY_CATEGORY
        searchCtxRepo.createSearchContext(CHAT_ID)

        searchCtxRepo.updateCategory(CHAT_ID, category)

        val ctx = searchCtxRepo.findById(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, ctx.chatId) },
            { assertEquals(category, ctx.category) }
        )
    }

    @Test
    fun `should update transaction id`() {
        searchCtxRepo.createSearchContext(CHAT_ID)

        searchCtxRepo.updateTrxId(CHAT_ID, TRX_ID)

        val ctx = searchCtxRepo.findById(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, ctx.chatId) },
            { assertEquals(TRX_ID, ctx.trxId) }
        )
    }

    @Test
    fun `should clear dialog state`() {
        searchCtxRepo.createSearchContext(CHAT_ID)

        searchCtxRepo.updateTransactionType(CHAT_ID, INCOME)
        searchCtxRepo.updateCategory(CHAT_ID, SALARY_CATEGORY)
        searchCtxRepo.updateTrxId(CHAT_ID, TRX_ID)

        searchCtxRepo.clearDialogState(CHAT_ID)

        val ctx = searchCtxRepo.findById(CHAT_ID)!!

        assertAll(
            { assertNull(ctx.type) },
            { assertNull(ctx.category) },
            { assertNull(ctx.trxId) }
        )
    }
}