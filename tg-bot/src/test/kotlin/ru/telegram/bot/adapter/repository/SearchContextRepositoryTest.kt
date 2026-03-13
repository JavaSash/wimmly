package ru.telegram.bot.adapter.repository

import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import ru.telegram.bot.adapter.PostgresTestContainer
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.TRX_ID
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.domain.tables.tables.SearchContext.Companion.SEARCH_CONTEXT
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@JooqTest
@Import(SearchContextRepository::class, ChatContextRepository::class)
class SearchContextRepositoryTest: PostgresTestContainer() {
    @Autowired
    lateinit var searchCtxRepo: SearchContextRepository
    @Autowired
    lateinit var chatContextRepository: ChatContextRepository

    @Autowired
    lateinit var dsl: DSLContext

    @BeforeEach
    fun clean() {
        dsl.deleteFrom(SEARCH_CONTEXT).execute()
        chatContextRepository.createUser(CHAT_ID)
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