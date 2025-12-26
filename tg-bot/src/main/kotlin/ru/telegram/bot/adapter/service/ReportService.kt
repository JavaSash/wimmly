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
                    percentage = if (totalAmount > BigDecimal.ZERO) {
                        val percent = (amount / totalAmount * 100.toBigDecimal())
                            .setScale(1, RoundingMode.HALF_UP)
                        "${percent}%"
                    } else "0%",
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
}