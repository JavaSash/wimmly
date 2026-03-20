package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository
import ru.telegram.bot.adapter.strategy.dto.SelectCategoryDto
import ru.telegram.bot.adapter.strategy.logic.common.CallbackChooser

@Component
class SelectCategoryChooser(
    private val chatContextRepository: ChatContextRepository,
    private val selectCategoryRepository: SelectCategoryRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
    private val searchContextRepository: SearchContextRepository,
) : CallbackChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        logger.info { "$$$ SelectCategoryChooser.execute for chatId: $chatId with callback: $callbackQuery" }
        val selectedCategoryCode = callbackQuery.data

        val categoriesDto: SelectCategoryDto = selectCategoryRepository.getData(chatId)
        val category: CategoryDto? = categoriesDto.categories.find { it.code == selectedCategoryCode }
        val flowCtx = chatContextRepository.getUser(chatId)?.flowContext
        logger.info { "$$$ [SelectCategoryChooser] process with flow context: $flowCtx" }

        if (category != null) {
            when(flowCtx) {
                StepCode.SEARCH_TRANSACTIONS.name -> searchContextRepository.updateCategory(chatId, category.code)
                StepCode.ADD_INCOME.name, StepCode.ADD_EXPENSE.name -> transactionDraftRepository.updateCategory(chatId, category.code)
                else -> {
                    logger.error { "$$$ Unsupported flow: $flowCtx for SelectCategoryChooser logic" }
                    return ExecuteStatus.NOTHING // todo error step?
                }
            }
            logger.info { "$$$ Category selected: code=${category.code}, desc=${category.description}" }
        } else {
            logger.error { "$$$ Unknown category code: $selectedCategoryCode" }
            return ExecuteStatus.NOTHING // todo error step?
        }
        return ExecuteStatus.FINAL
    }
}