package ru.telegram.bot.adapter.strategy.dto

import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME

data class SelectCategoryDto(
    val txType: String = INCOME,
    val categories: List<CategoryDto>
): DataModel
