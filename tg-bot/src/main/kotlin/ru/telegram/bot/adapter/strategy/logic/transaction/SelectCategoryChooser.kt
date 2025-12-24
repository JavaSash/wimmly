package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.data.SelectCategoryRepository
import ru.telegram.bot.adapter.strategy.logic.CallbackChooser

@Component
class SelectCategoryChooser(
    private val usersRepository: UsersRepository,
    private val selectCategoryRepository: SelectCategoryRepository,
) : CallbackChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        logger.info { "$$$ SelectCategoryChooser.execute for chatId: $chatId with callback: $callbackQuery" }
        val selectedCategoryCode = callbackQuery.data

        val categoriesDto = selectCategoryRepository.getData(chatId)
        val category = categoriesDto.categories.find { it.code == selectedCategoryCode }

        if (category != null) {
            usersRepository.updateCategory(chatId, category.code)
            usersRepository.updateUserStep(chatId, StepCode.ENTER_AMOUNT)
            logger.info { "$$$ Category selected: code=${category.code}, desc=${category.description}" }
        } else {
            logger.error { "$$$ Unknown category code: $selectedCategoryCode" }
            return ExecuteStatus.NOTHING
        }
        return ExecuteStatus.FINAL
    }
}