package ru.wimme.logic.api

import org.springframework.web.bind.annotation.*
import ru.wimme.logic.model.transaction.*
import java.util.*

@RestController
@RequestMapping("/api/category")
class CategoryApi {

    @GetMapping("/{type}")
    fun getCategoriesByType(@PathVariable type: TransactionType): List<CategoryDto> {
        return when (type) {
            TransactionType.EXPENSE -> ExpenseCategory.values().map {
                CategoryDto(it.name, it.description, TransactionType.EXPENSE)
            }
            TransactionType.INCOME -> IncomeCategory.values().map {
                CategoryDto(it.name, it.description, TransactionType.INCOME)
            }
        }
    }

    @GetMapping("/all")
    fun getAllCategories(): Map<TransactionType, List<CategoryDto>> {
        return mapOf(
            TransactionType.EXPENSE to ExpenseCategory.values().map {
                CategoryDto(it.name, it.description, TransactionType.EXPENSE)
            },
            TransactionType.INCOME to IncomeCategory.values().map {
                CategoryDto(it.name, it.description, TransactionType.INCOME)
            }
        )
    }
}