package ru.telegram.bot.adapter.repository

import mu.KLogging
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.domain.tables.tables.TransactionDraft.Companion.TRANSACTION_DRAFT
import ru.telegram.bot.adapter.domain.tables.tables.pojos.TransactionDraft
import java.math.BigDecimal
import java.time.Instant

@Repository
class TransactionDraftRepository(private val dslContext: DSLContext) {// todo iml with upd all needed fields
    companion object : KLogging()

    fun createTransactionDraft(chatId: Long): TransactionDraft {
        logger.info { "$$$ createTransactionDraft for chat $chatId" }
        val record = dslContext.newRecord(
            TRANSACTION_DRAFT, TransactionDraft(chatId = chatId)
        )
        record.store()
        return record.into(TransactionDraft::class.java)
    }

    fun getTransactionDraft(chatId: Long): TransactionDraft? =
        dslContext.selectFrom(TRANSACTION_DRAFT)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId))
            .fetchOneInto(TransactionDraft::class.java)

    fun updateCategory(chatId: Long, category: String) {
        dslContext.update(TRANSACTION_DRAFT)
            .set(TRANSACTION_DRAFT.CATEGORY, category)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId)).execute()
    }

    fun updateTransactionType(chatId: Long, txType: String) {
        dslContext.update(TRANSACTION_DRAFT)
            .set(TRANSACTION_DRAFT.TYPE, txType)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId)).execute()
    }

    fun updateTransactionDate(chatId: Long, date: Instant) {
        dslContext.update(TRANSACTION_DRAFT)
            .set(TRANSACTION_DRAFT.DATE, date)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId)).execute()
    }

    fun updateAmount(chatId: Long, amount: BigDecimal) {
        logger.info { "$$$ Updating amount for chat $chatId: $amount" }

        dslContext.update(TRANSACTION_DRAFT)
            .set(TRANSACTION_DRAFT.AMOUNT, amount)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId)).execute()
    }

    fun updateComment(chatId: Long, comment: String?) {
        dslContext.update(TRANSACTION_DRAFT)
            .set(TRANSACTION_DRAFT.COMMENT, comment)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId)).execute()
    }

    fun clearDialogState(chatId: Long) {
        dslContext.update(TRANSACTION_DRAFT)
            .set(TRANSACTION_DRAFT.TYPE, null as String?)
            .set(TRANSACTION_DRAFT.CATEGORY, null as String?)
            .set(TRANSACTION_DRAFT.AMOUNT, null as BigDecimal?)
            .set(TRANSACTION_DRAFT.DATE, null as Instant?)
            .set(TRANSACTION_DRAFT.COMMENT, null as String?)
            .where(TRANSACTION_DRAFT.CHAT_ID.eq(chatId))
            .execute()
    }
}