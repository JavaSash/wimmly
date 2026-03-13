package ru.telegram.bot.adapter

import org.jooq.DSLContext
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import ru.telegram.bot.adapter.domain.tables.tables.ChatContext
import ru.telegram.bot.adapter.domain.tables.tables.SearchContext
import ru.telegram.bot.adapter.domain.tables.tables.TransactionDraft
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository

/**
 * Up only DataSource, jOOQ DSLContext, transaction manager for test
 */
@JooqTest
@Import(ChatContextRepository::class, SearchContextRepository::class, TransactionDraftRepository::class)
abstract class DbBasicTest : PostgresTestContainer() {
    @Autowired
    lateinit var chatCtxRepo: ChatContextRepository

    @Autowired
    lateinit var searchCtxRepo: SearchContextRepository

    @Autowired
    lateinit var trxDraftRepo: TransactionDraftRepository

    @Autowired
    lateinit var dsl: DSLContext

    @AfterEach
    fun clean() {
        dsl.deleteFrom(SearchContext.SEARCH_CONTEXT).execute()
        dsl.deleteFrom(TransactionDraft.TRANSACTION_DRAFT).execute()
        dsl.deleteFrom(ChatContext.CHAT_CONTEXT).execute()
    }
}