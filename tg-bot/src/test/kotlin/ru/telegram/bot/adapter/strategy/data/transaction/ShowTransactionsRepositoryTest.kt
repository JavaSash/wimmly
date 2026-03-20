package ru.telegram.bot.adapter.strategy.data.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.UNKNOWN_FLOW
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.formSearchContext
import ru.telegram.bot.adapter.formTransactionRs
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@ExtendWith(MockitoExtension::class)
class ShowTransactionsRepositoryTest {
    @Mock
    lateinit var transactionService: TransactionService

    @Mock
    lateinit var searchContextRepository: SearchContextRepository

    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    private lateinit var repository: ShowTransactionsRepository

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        repository = ShowTransactionsRepository(
            transactionService,
            searchContextRepository,
            chatContextRepository
        )
    }

    @Test
    fun `getData should return transactions for SEARCH_TRANSACTIONS flow`() {
        val searchCtx = formSearchContext()
        val chatCtx = formChatContext()
        val tx = formTransactionRs(type = INCOME, category = SALARY_CATEGORY)

        whenever(searchContextRepository.findById(chatId)).thenReturn(searchCtx)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(chatCtx)
        whenever(transactionService.getTransactionsWithFilters(any())).thenReturn(listOf(tx))

        val result = repository.getData(chatId)
        val item = result.transactions.first()
        assertAll(
            { assertEquals(1, result.transactions.size) },
            { assertEquals(tx.displayId, item.displayId) },
            { assertEquals(tx.category, item.category) },
            { assertEquals(tx.type, item.type) },
            { assertEquals(tx.comment, item.comment) },
            { verify(transactionService).getTransactionsWithFilters(any()) }
        )
    }

    @Test
    fun `getData should return transaction for DELETE_TRANSACTION flow`() {
        val searchCtx = formSearchContext()
        val chatCtx = formChatContext(flowContext = StepCode.DELETE_TRANSACTION.name)
        val tx = formTransactionRs(type = EXPENSE, category = FOOD_CATEGORY)

        whenever(searchContextRepository.findById(chatId)).thenReturn(searchCtx)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(chatCtx)
        whenever(transactionService.getUserTransactionByDisplayId(chatId, searchCtx.trxId!!)).thenReturn(tx)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(1, result.transactions.size) },
            { assertEquals(searchCtx.trxId, result.transactions.first().displayId) }
        )
    }

    @Test
    fun `getData should return empty list when DELETE_TRANSACTION and not found`() {
        val searchCtx = formSearchContext()
        val chatCtx = formChatContext(flowContext = StepCode.DELETE_TRANSACTION.name)

        whenever(searchContextRepository.findById(chatId)).thenReturn(searchCtx)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(chatCtx)
        whenever(transactionService.getUserTransactionByDisplayId(chatId, searchCtx.trxId!!)).thenReturn(null)

        val result = repository.getData(chatId)

        assertTrue(result.transactions.isEmpty())
    }

    @Test
    fun `getData should return empty list for unsupported flow`() {
        val searchCtx = formSearchContext()
        val chatCtx = formChatContext(flowContext = UNKNOWN_FLOW)

        whenever(searchContextRepository.findById(chatId)).thenReturn(searchCtx)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(chatCtx)

        val result = repository.getData(chatId)

        assertAll(
            { assertTrue(result.transactions.isEmpty()) },
            { verify(transactionService, never()).getTransactionsWithFilters(any()) },
            { verify(transactionService, never()).getUserTransactionByDisplayId(any(), any()) },
        )
    }
}