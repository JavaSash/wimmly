package ru.telegram.bot.adapter.repository

import mu.KLogging
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.domain.tables.tables.SearchContext.Companion.SEARCH_CONTEXT
import ru.telegram.bot.adapter.domain.tables.tables.pojos.SearchContext
/*
todo iml with upd all needed fields
 */
@Repository
class SearchContextRepository(private val dslContext: DSLContext) {
    companion object : KLogging()

    fun createSearchContext(chatId: Long): SearchContext {
        logger.info { "$$$ createSearchContext for chat $chatId" }

        val record = dslContext.newRecord(
            SEARCH_CONTEXT,
            SearchContext(chatId = chatId)
        )
        record.store()
        return record.into(SearchContext::class.java)
    }

    fun updateTransactionType(chatId: Long, txType: String) {
        dslContext.update(SEARCH_CONTEXT)
            .set(SEARCH_CONTEXT.TYPE, txType)
            .where(SEARCH_CONTEXT.CHAT_ID.eq(chatId)).execute()
    }

    fun findById(chatId: Long): SearchContext? =
        dslContext.selectFrom(SEARCH_CONTEXT).where(SEARCH_CONTEXT.CHAT_ID.eq(chatId))
            .fetchOneInto(SearchContext::class.java)

    fun updateCategory(chatId: Long, category: String) {
        dslContext.update(SEARCH_CONTEXT)
            .set(SEARCH_CONTEXT.CATEGORY, category)
            .where(SEARCH_CONTEXT.CHAT_ID.eq(chatId)).execute()
    }

    fun updateTrxId(chatId: Long, trxId: Long) {
        dslContext.update(SEARCH_CONTEXT)
            .set(SEARCH_CONTEXT.TRX_ID, trxId)
            .where(SEARCH_CONTEXT.CHAT_ID.eq(chatId)).execute()
    }

    fun clearDialogState(chatId: Long) {
        dslContext.update(SEARCH_CONTEXT)
            .setNull(SEARCH_CONTEXT.TRX_ID)
            .setNull(SEARCH_CONTEXT.TYPE)
            .setNull(SEARCH_CONTEXT.CATEGORY)
            .where(SEARCH_CONTEXT.CHAT_ID.eq(chatId))
            .execute()
    }
}