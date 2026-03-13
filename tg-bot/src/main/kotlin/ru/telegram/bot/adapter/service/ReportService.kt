package ru.telegram.bot.adapter.service

import org.springframework.stereotype.Service
import ru.telegram.bot.adapter.dto.view.CategoryInfo
import ru.telegram.bot.adapter.utils.formatMoney
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class ReportService {

    fun prepareCategories(
        categories: Map<String, BigDecimal>,
        totalAmount: BigDecimal,
        isExpense: Boolean = false
    ): List<CategoryInfo> {
        if (categories.isEmpty()) {
            return emptyList()
        }

        return categories.entries
            .sortedByDescending { it.value }
            .map { (categoryName, amount) ->
                CategoryInfo(
                    name = cleanCategoryName(categoryName),
                    amount = amount,
                    formattedAmount = amount.formatMoney(),
                    percentage = calculatePercentage(totalAmount = totalAmount, amount = amount),
                    isExpense = isExpense
                )
            }
    }

    private fun cleanCategoryName(categoryString: String): String {
        return when {
            // Если строка содержит "description=", извлекаем значение
            categoryString.contains("description=") -> {
                categoryString.substringAfter("description=")
                    .substringBefore(")")
                    .substringBefore(",")
                    .trim()
            }
            // Если это просто название категории, возвращаем как есть
            else -> categoryString
        }
    }

    private fun calculatePercentage(totalAmount: BigDecimal, amount: BigDecimal): String =
        if (totalAmount > BigDecimal.ZERO) {
            val percent = amount.multiply(100.toBigDecimal())
                .divide(totalAmount, 1, RoundingMode.HALF_UP)
            "${percent}%"
        } else "0%"
}