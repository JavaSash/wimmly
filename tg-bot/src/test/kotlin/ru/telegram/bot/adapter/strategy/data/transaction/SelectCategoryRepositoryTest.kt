package ru.telegram.bot.adapter.strategy.data.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY_DTO
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY_DTO
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.client.CategoryClient
import ru.telegram.bot.adapter.domain.tables.tables.pojos.TransactionDraft
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

@ExtendWith(MockitoExtension::class)
class SelectCategoryRepositoryTest {
    @Mock
    lateinit var categoryClient: CategoryClient
    @Mock
    lateinit var transactionDraftRepository: TransactionDraftRepository

    private lateinit var repository: SelectCategoryRepository

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        repository = SelectCategoryRepository(categoryClient, transactionDraftRepository)
    }

    @ParameterizedTest
    @ValueSource(strings = [INCOME, EXPENSE])
    fun `getData should return categories from client for transaction`(txType: String) {
        val draft = TransactionDraft(type = txType, chatId = chatId)
        val categories = if (txType == INCOME) listOf(SALARY_CATEGORY_DTO) else listOf(FOOD_CATEGORY_DTO)

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(txType)).thenReturn(categories)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(txType, result.txType) },
            { assertEquals(categories, result.categories) }
        )
    }

    @Test
    fun `getData should fallback to INCOME when draft is null`() {
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(null)
        whenever(categoryClient.getCategories(INCOME))
            .thenReturn(SelectCategoryRepository.INCOME_CATEGORIES_STUB)

        val result = repository.getData(chatId)

        assertEquals(INCOME, result.txType)
    }

    @Test
    fun `getData should return INCOME stub when client fails`() {
        val draft = TransactionDraft(type = INCOME, chatId = chatId)

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(INCOME)).thenThrow(RuntimeException())

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(INCOME, result.txType) },
            { assertEquals(SelectCategoryRepository.INCOME_CATEGORIES_STUB, result.categories) }
        )
    }

    @Test
    fun `getData should return EXPENSE stub when client fails`() {
        val draft = TransactionDraft(type = EXPENSE)

        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(draft)
        whenever(categoryClient.getCategories(EXPENSE)).thenThrow(RuntimeException())

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(EXPENSE, result.txType) },
            { assertEquals(SelectCategoryRepository.EXPENSE_CATEGORIES_STUB, result.categories) }
        )
    }
}