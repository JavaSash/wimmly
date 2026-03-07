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

        val categoriesDto = selectCategoryRepository.getData(chatId)
        val category: CategoryDto? = categoriesDto.categories.find { it.code == selectedCategoryCode }

        if (category != null) {
            transactionDraftRepository.updateCategory(chatId, category.code)
            if (StepCode.SEARCH_TRANSACTIONS.name == chatContextRepository.getUser(chatId)?.flowContext) {
                searchContextRepository.updateCategory(chatId, category.code)
            } else {
                transactionDraftRepository.updateCategory(chatId, category.code)
            } // todo else if with ENTER_AMOUNT and else with error (flow is null)

            logger.info { "$$$ Category selected: code=${category.code}, desc=${category.description}" }
        } else {
            logger.error { "$$$ Unknown category code: $selectedCategoryCode" }
            return ExecuteStatus.NOTHING
        }
        return ExecuteStatus.FINAL
    }
}