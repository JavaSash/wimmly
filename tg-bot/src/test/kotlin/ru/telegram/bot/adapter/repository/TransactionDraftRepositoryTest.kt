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
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.COMMENT
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.domain.tables.tables.TransactionDraft.Companion.TRANSACTION_DRAFT
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

@JooqTest
@Import(TransactionDraftRepository::class, ChatContextRepository::class)
class TransactionDraftRepositoryTest : PostgresTestContainer() {

    @Autowired
    lateinit var trxDraftRepo: TransactionDraftRepository

    @Autowired
    lateinit var chatContextRepository: ChatContextRepository

    @Autowired
    lateinit var dsl: DSLContext

    @BeforeEach
    fun clean() {
        dsl.deleteFrom(TRANSACTION_DRAFT).execute()
        chatContextRepository.createUser(CHAT_ID)
    }

    @Test
    fun `should create transaction draft`() {
        val draft = trxDraftRepo.createTransactionDraft(CHAT_ID)

        assertAll(
            { assertEquals(CHAT_ID, draft.chatId) },
            { assertNull(draft.type) },
            { assertNull(draft.category) },
            { assertNull(draft.amount) },
            { assertNull(draft.date) },
            { assertNull(draft.comment) },
        )
    }

    @Test
    fun `getTransactionDraft should return null when draft does not exist`() {
        assertNull(trxDraftRepo.getTransactionDraft(999))
    }

    @Test
    fun `getTransactionDraft should return draft`() {
        trxDraftRepo.createTransactionDraft(CHAT_ID)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)

        assertAll(
            { assertNotNull(draft) },
            { assertEquals(CHAT_ID, draft!!.chatId) }
        )
    }

    @Test
    fun `should update category`() {
        trxDraftRepo.createTransactionDraft(CHAT_ID)

        trxDraftRepo.updateCategory(CHAT_ID, SALARY_CATEGORY)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, draft.chatId) },
            { assertEquals(SALARY_CATEGORY, draft.category) }
        )
    }

    @Test
    fun `should update transaction type`() {
        trxDraftRepo.createTransactionDraft(CHAT_ID)

        trxDraftRepo.updateTransactionType(CHAT_ID, INCOME)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, draft.chatId) },
            { assertEquals(INCOME, draft.type) }
        )
    }

    @Test
    fun `should update transaction date`() {
        val date = Instant.now().truncatedTo(ChronoUnit.MICROS)
        trxDraftRepo.createTransactionDraft(CHAT_ID)

        trxDraftRepo.updateTransactionDate(CHAT_ID, date)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, draft.chatId) },
            { assertEquals(date, draft.date) }
        )
    }

    @Test
    fun `should update amount`() {
        val amount = BigDecimal("123.45")
        trxDraftRepo.createTransactionDraft(CHAT_ID)

        trxDraftRepo.updateAmount(CHAT_ID, amount)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, draft.chatId) },
            { assertEquals(amount, draft.amount) }
        )
    }

    @Test
    fun `should update comment`() {
        val comment = "salary payment"
        trxDraftRepo.createTransactionDraft(CHAT_ID)

        trxDraftRepo.updateComment(CHAT_ID, comment)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, draft.chatId) },
            { assertEquals(comment, draft.comment) }
        )
    }

    @Test
    fun `should clear dialog state`() {
        val date = Instant.now()
        val amount = AMOUNT_100
        val comment = COMMENT

        trxDraftRepo.createTransactionDraft(CHAT_ID)

        trxDraftRepo.updateTransactionType(CHAT_ID, INCOME)
        trxDraftRepo.updateCategory(CHAT_ID, SALARY_CATEGORY)
        trxDraftRepo.updateAmount(CHAT_ID, amount)
        trxDraftRepo.updateTransactionDate(CHAT_ID, date)
        trxDraftRepo.updateComment(CHAT_ID, comment)

        trxDraftRepo.clearDialogState(CHAT_ID)

        val draft = trxDraftRepo.getTransactionDraft(CHAT_ID)!!

        assertAll(
            { assertNull(draft.type) },
            { assertNull(draft.category) },
            { assertNull(draft.amount) },
            { assertNull(draft.date) },
            { assertNull(draft.comment) }
        )
    }
}