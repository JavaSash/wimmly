package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.UNKNOWN_FLOW
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY_DTO
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY_DTO
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formCallbackQuery
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository
import ru.telegram.bot.adapter.strategy.dto.SelectCategoryDto
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE

@ExtendWith(MockitoExtension::class)
class SelectCategoryChooserTest {
    @Mock
    private lateinit var chatContextRepository: ChatContextRepository
    @Mock
    private lateinit var selectCategoryRepository: SelectCategoryRepository
    @Mock
    private lateinit var transactionDraftRepository: TransactionDraftRepository
    @Mock
    private lateinit var searchContextRepository: SearchContextRepository

    private lateinit var chooser: SelectCategoryChooser

    private val chatId = CHAT_ID
    private val categoryCodeExpense = FOOD_CATEGORY_DTO.code
    private val categoryDescExpense = FOOD_CATEGORY_DTO.description
    private val txTypeExpense = FOOD_CATEGORY_DTO.type
    private val expenseCategory = CategoryDto(categoryCodeExpense, categoryDescExpense, type = txTypeExpense)
    private val selectCategoryDtoExpense =
        SelectCategoryDto(txType = txTypeExpense, categories = listOf(expenseCategory))
    private val categoryCodeIncome = SALARY_CATEGORY_DTO.code
    private val categoryDescIncome = SALARY_CATEGORY_DTO.description
    private val txTypeIncome = SALARY_CATEGORY_DTO.type
    private val incomeCategory = CategoryDto(categoryCodeIncome, categoryDescIncome, type = txTypeIncome)
    private val selectCategoryDtoIncome = SelectCategoryDto(txType = txTypeIncome, categories = listOf(incomeCategory))

    @BeforeEach
    fun setup() {
        chooser = SelectCategoryChooser(
            chatContextRepository = chatContextRepository,
            selectCategoryRepository = selectCategoryRepository,
            transactionDraftRepository = transactionDraftRepository,
            searchContextRepository = searchContextRepository
        )
    }

    @Test
    fun `execute should select category and update search context for SEARCH_TRANSACTIONS flow`() {
        val flowContext = StepCode.SEARCH_TRANSACTIONS.name
        val user = formChatContext(flowContext = flowContext)
        val callbackQuery = formCallbackQuery(data = categoryCodeExpense)
        whenever(selectCategoryRepository.getData(chatId)).thenReturn(selectCategoryDtoExpense)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = chooser.execute(chatId, callbackQuery)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(transactionDraftRepository, never()).updateCategory(any(), any()) },
            { verify(searchContextRepository).updateCategory(chatId, categoryCodeExpense) },
            { verify(selectCategoryRepository).getData(chatId) },
            { verify(chatContextRepository).getUser(chatId) },
        )
    }

    @Test
    fun `execute should update transaction draft for ADD_EXPENSE flow`() {
        val flowContext = StepCode.ADD_EXPENSE.name
        val callback = formCallbackQuery(categoryCodeExpense)
        val user = formChatContext(flowContext = flowContext)
        whenever(selectCategoryRepository.getData(chatId)).thenReturn(selectCategoryDtoExpense)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(transactionDraftRepository).updateCategory(chatId, categoryCodeExpense) },
            { verify(searchContextRepository, never()).updateCategory(any(), any()) },
            { verify(selectCategoryRepository).getData(chatId) },
            { verify(chatContextRepository).getUser(chatId) },
        )
    }

    @Test
    fun `execute should update transaction draft for ADD_INCOME flow`() {
        val flowContext = StepCode.ADD_EXPENSE.name
        val callback = formCallbackQuery(categoryCodeIncome)
        val user = formChatContext(flowContext = flowContext)
        whenever(selectCategoryRepository.getData(chatId)).thenReturn(selectCategoryDtoIncome)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(transactionDraftRepository).updateCategory(chatId, categoryCodeIncome) },
            { verify(selectCategoryRepository).getData(chatId) },
            { verify(chatContextRepository).getUser(chatId) },
        )
    }

    @Test
    fun `execute should return NOTHING when category not found`() {
        val flowContext = StepCode.SEARCH_TRANSACTIONS.name
        val callback = formCallbackQuery("UNKNOWN")
        val user = formChatContext(flowContext = flowContext)

        whenever(selectCategoryRepository.getData(chatId)).thenReturn(
            SelectCategoryDto(
                txType = EXPENSE,
                categories = emptyList()
            )
        )
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.NOTHING, result) },
            { verify(transactionDraftRepository, never()).updateCategory(any(), any()) },
            { verify(searchContextRepository, never()).updateCategory(any(), any()) },
            { verify(selectCategoryRepository).getData(chatId) },
            { verify(chatContextRepository).getUser(chatId) },
        )
    }

    @Test
    fun `execute should return NOTHING for unsupported flow`() {
        val flowContext = UNKNOWN_FLOW
        val callback = formCallbackQuery(categoryCodeExpense)
        val user = formChatContext(flowContext = flowContext)
        whenever(selectCategoryRepository.getData(chatId)).thenReturn(selectCategoryDtoExpense)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.NOTHING, result) },
            { verify(transactionDraftRepository, never()).updateCategory(any(), any()) },
            { verify(searchContextRepository, never()).updateCategory(any(), any()) },
            { verify(selectCategoryRepository).getData(chatId) },
            { verify(chatContextRepository).getUser(chatId) },
        )
    }
}