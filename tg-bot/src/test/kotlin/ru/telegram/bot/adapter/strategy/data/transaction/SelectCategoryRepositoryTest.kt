package ru.telegram.bot.adapter.strategy.data.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.TestConstants.Chat.UNKNOWN_FLOW
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY_DTO
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY_DTO
import ru.telegram.bot.adapter.client.CategoryClient
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.formSearchContext
import ru.telegram.bot.adapter.formTransactionDraft
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository.Companion.EXPENSE_CATEGORIES_STUB
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository.Companion.INCOME_CATEGORIES_STUB
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@ExtendWith(MockitoExtension::class)
class SelectCategoryRepositoryTest {
    @Mock
    lateinit var categoryClient: CategoryClient

    @Mock
    lateinit var transactionDraftRepository: TransactionDraftRepository

    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    @Mock
    lateinit var searchContextRepository: SearchContextRepository

    private lateinit var repository: SelectCategoryRepository

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        repository = SelectCategoryRepository(
            categoryClient,
            transactionDraftRepository,
            chatContextRepository,
            searchContextRepository
        )
    }

    @Test
    fun `getData should use searchContext type for SEARCH_TRANSACTIONS flow`() {
        val categoryDto = FOOD_CATEGORY_DTO
        val searchCtx = formSearchContext()
        val user = formChatContext(flowContext = StepCode.SEARCH_TRANSACTIONS.name)

        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)
        whenever(searchContextRepository.findById(chatId)).thenReturn(searchCtx)
        whenever(categoryClient.getCategories(EXPENSE)).thenReturn(listOf(categoryDto))

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(EXPENSE, result.txType) },
            { assertEquals(listOf(FOOD_CATEGORY_DTO), result.categories) },
            { verify(chatContextRepository).getUser(chatId) },
            { verify(searchContextRepository).findById(chatId) },
            { verify(categoryClient).getCategories(categoryDto.type) },
            { verify(transactionDraftRepository, never()).getTransactionDraft(any()) },
        )
    }

    @Test
    fun `getData should return categories from client for ADD_INCOME`() {
        val txType = INCOME
        val flowCtx = StepCode.ADD_INCOME.name
        val user = formChatContext(flowContext = flowCtx)
        val draft = formTransactionDraft(type = txType)
        val categories = listOf(SALARY_CATEGORY_DTO)

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(txType)).thenReturn(categories)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { verify(searchContextRepository, never()).findById(any()) },
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
        )
    }

    @Test
    fun `getData should return categories from client for ADD_EXPENSE`() {
        val txType = EXPENSE
        val flowCtx = StepCode.ADD_EXPENSE.name
        val user = formChatContext(flowContext = flowCtx)
        val draft = formTransactionDraft(type = txType)
        val categories = listOf(FOOD_CATEGORY_DTO)

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(txType)).thenReturn(categories)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { verify(searchContextRepository, never()).findById(any()) },
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
        )
    }

    @Test
    fun `getData should fallback to INCOME for unknown flow`() {
        val txType = INCOME // default
        val flowCtx = UNKNOWN_FLOW
        val user = formChatContext(flowContext = flowCtx)
        val categories = INCOME_CATEGORIES_STUB // default
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)
        whenever(categoryClient.getCategories(txType)).thenReturn(categories)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(searchContextRepository, never()).findById(any()) },
            { verify(transactionDraftRepository, never()).getTransactionDraft(any()) },
        )
    }

    @Test
    fun `getData should fallback to INCOME when txType is null for add transaction flow`() {
        val txType = INCOME // default
        val flowCtx = StepCode.ADD_INCOME.name
        val user = formChatContext(flowContext = flowCtx)
        val categories = INCOME_CATEGORIES_STUB // default
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(null)
        whenever(categoryClient.getCategories(txType)).thenReturn(categories)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(searchContextRepository, never()).findById(any()) },
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
        )
    }

    @Test
    fun `getData should fallback to INCOME when txType is null for SEARCH_TRANSACTIONS flow`() {
        val txType = INCOME // default
        val flowCtx = StepCode.SEARCH_TRANSACTIONS.name
        val user = formChatContext(flowContext = flowCtx)
        val categories = INCOME_CATEGORIES_STUB // default
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)
        whenever(searchContextRepository.findById(chatId)).thenReturn(null)
        whenever(categoryClient.getCategories(txType)).thenReturn(categories)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(searchContextRepository).findById(chatId) },
            { verify(transactionDraftRepository, never()).getTransactionDraft(any()) },
        )
    }

    @Test
    fun `getData should return EXPENSE stub when client fails for SEARCH_TRANSACTIONS`() {
        val categoryDto = FOOD_CATEGORY_DTO
        val searchCtx = formSearchContext()
        val user = formChatContext(flowContext = StepCode.SEARCH_TRANSACTIONS.name)
        val categories = EXPENSE_CATEGORIES_STUB

        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)
        whenever(searchContextRepository.findById(chatId)).thenReturn(searchCtx)
        whenever(categoryClient.getCategories(EXPENSE)).thenThrow(RuntimeException())

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(EXPENSE, result.txType) },
            { assertEquals(categories, result.categories) },
            { verify(chatContextRepository).getUser(chatId) },
            { verify(searchContextRepository).findById(chatId) },
            { verify(categoryClient).getCategories(categoryDto.type) },
            { verify(transactionDraftRepository, never()).getTransactionDraft(any()) },
        )
    }

    @Test
    fun `getData should return INCOME stub when client fails`() {
        val txType = INCOME
        val flowCtx = StepCode.ADD_INCOME.name
        val user = formChatContext(flowContext = flowCtx)
        val draft = formTransactionDraft(type = txType)
        val categories = INCOME_CATEGORIES_STUB

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(txType)).thenThrow(RuntimeException())
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { verify(searchContextRepository, never()).findById(any()) },
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
        )
    }

    @Test
    fun `getData should return EXPENSE stub when client fails`() {
        val txType = EXPENSE
        val flowCtx = StepCode.ADD_EXPENSE.name
        val user = formChatContext(flowContext = flowCtx)
        val draft = formTransactionDraft(type = txType)
        val categories = EXPENSE_CATEGORIES_STUB

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(txType)).thenThrow(RuntimeException())
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { assertEquals(categories, result.categories) },
            { verify(categoryClient).getCategories(txType) },
            { verify(chatContextRepository).getUser(chatId) },
            { verify(searchContextRepository, never()).findById(any()) },
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
        )
    }
}