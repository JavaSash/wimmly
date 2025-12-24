package ru.telegram.bot.adapter.strategy.data

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.client.CategoryClient
import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.repository.UsersRepository
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
    private val usersRepository: UsersRepository
) : AbstractRepository<SelectCategoryDto>() {
    companion object : KLogging()

    /**
     * @return categories from budget-service or stub
     */
    override fun getData(chatId: Long): SelectCategoryDto {
        logger.info { "$$$ Try to get categories for chat: $chatId" }
        val dialogData = usersRepository.getUser(chatId)
        val txType = dialogData?.transactionType ?: INCOME
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
        val categories = if (txType == INCOME) {
            listOf(
                CategoryDto("SALARY", "Зарплата", INCOME),
                CategoryDto("INVESTMENT", "Инвестиции", INCOME),
                CategoryDto("SAVINGS", "Сбережения", INCOME),
                CategoryDto("OTHER", "Прочее", INCOME)
            )
        } else {
            listOf(
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
        return SelectCategoryDto(categories = categories, txType = txType)
    }
}