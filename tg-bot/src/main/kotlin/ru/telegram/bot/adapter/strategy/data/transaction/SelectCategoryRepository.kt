package ru.telegram.bot.adapter.strategy.data.transaction

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.client.CategoryClient
import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.data.common.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.SelectCategoryDto
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

/**
 * Data provider for categories
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 * T should extend from DataModel
 */
@Repository
class SelectCategoryRepository(
    private val categoryClient: CategoryClient,
    private val transactionDraftRepository: TransactionDraftRepository,
    private val chatContextRepository: ChatContextRepository,
    private val searchContextRepository: SearchContextRepository
) : AbstractRepository<SelectCategoryDto>() {
    companion object : KLogging() {
        val INCOME_CATEGORIES_STUB: List<CategoryDto> = listOf(
            CategoryDto("SALARY", "Зарплата", INCOME),
            CategoryDto("INVESTMENT", "Инвестиции", INCOME),
            CategoryDto("SAVINGS", "Сбережения", INCOME),
            CategoryDto("OTHER", "Прочее", INCOME)
        )

        val EXPENSE_CATEGORIES_STUB: List<CategoryDto> = listOf(
            CategoryDto("HYGIENE", "Гигиена", EXPENSE),
            CategoryDto("FOOD", "Еда", EXPENSE),
            CategoryDto("HOME", "Дом", EXPENSE),
            CategoryDto("HEALTH", "Здоровье", EXPENSE),
            CategoryDto("CAFE", "Кафе", EXPENSE),
            CategoryDto("EDUCATION", "Образование", EXPENSE),
            CategoryDto("TRANSPORT", "Транспорт", EXPENSE),
            CategoryDto("CAR", "Машина", EXPENSE),
            CategoryDto("CLOTHES", "Одежда", EXPENSE),
            CategoryDto("PETS", "Питомцы", EXPENSE),
            CategoryDto("COMMUNICATION", "Связь", EXPENSE),
            CategoryDto("SPORT", "Спорт", EXPENSE),
            CategoryDto("ENTERTAINMENT", "Развлечения", EXPENSE),
            CategoryDto("OTHER", "Прочее", EXPENSE)
        )
    }

    /**
     * @return categories from budget-service or stub
     */
    override fun getData(chatId: Long): SelectCategoryDto {
        val flowCtx = chatContextRepository.getUser(chatId)?.flowContext
        logger.info { "$$$ Try to get categories for chat: $chatId and flow: $flowCtx" }

        val txType = when (flowCtx) {
            StepCode.SEARCH_TRANSACTIONS.name -> searchContextRepository.findById(chatId)?.type
            StepCode.ADD_INCOME.name, StepCode.ADD_EXPENSE.name -> transactionDraftRepository.getTransactionDraft(chatId)?.type
            else -> INCOME
        } ?: INCOME // impossible
        return runCatching {
            SelectCategoryDto(
                categories = categoryClient.getCategories(txType),
                txType = txType
            )
        }
            .onFailure { logger.error { "$$$ Can't receive categories for chat: $chatId. Cause: ${it.message}. Return stub" } }
            .getOrDefault(getCategoriesStub(txType))
    }

    private fun getCategoriesStub(txType: String): SelectCategoryDto {
        val categories = if (txType == INCOME) INCOME_CATEGORIES_STUB else EXPENSE_CATEGORIES_STUB
        return SelectCategoryDto(categories = categories, txType = txType)
    }
}