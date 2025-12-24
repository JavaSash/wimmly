package ru.wimme.logic.model.transaction

sealed class TransactionCategory {
    abstract val code: String
    abstract val description: String
    abstract val type: TransactionType

    data class Income(
        override val code: String,
        override val description: String
    ) : TransactionCategory() {
        override val type: TransactionType = TransactionType.INCOME
    }

    data class Expense(
        override val code: String,
        override val description: String
    ) : TransactionCategory() {
        override val type: TransactionType = TransactionType.EXPENSE
    }

    companion object {
        fun fromCode(code: String, type: TransactionType): TransactionCategory {
            return when (type) {
                TransactionType.INCOME -> {
                    val enumValue = IncomeCategory.values().find { it.name == code }
                    Income(
                        code = code,
                        description = enumValue?.description ?: code
                    )
                }
                TransactionType.EXPENSE -> {
                    val enumValue = ExpenseCategory.values().find { it.name == code }
                    Expense(
                        code = code,
                        description = enumValue?.description ?: code
                    )
                }
            }
        }
        // Дополнительный метод для создания из строки (если тип неизвестен)
        fun fromCode(code: String): TransactionCategory? {
            return try {
                // Пробуем найти как категорию доходов
                val incomeEnum = IncomeCategory.values().find { it.name == code }
                if (incomeEnum != null) {
                    return Income(
                        code = incomeEnum.name,
                        description = incomeEnum.description
                    )
                }
                // Пробуем найти как категорию расходов
                val expenseEnum = ExpenseCategory.values().find { it.name == code }
                if (expenseEnum != null) {
                    return Expense(
                        code = expenseEnum.name,
                        description = expenseEnum.description
                    )
                }
                // Не нашли в enum'ах
                null
            } catch (e: Exception) {
                null
            }
        }
    }
}

enum class ExpenseCategory(
    val description: String
) {
    HYGIENE("Гигиена"),
    FOOD("Еда"),
    HOME("Дом"),
    HEALTH("Здоровье"),
    CAFE("Кафе"),
    EDUCATION("Образование"),
    TRANSPORT("Транспорт"),
    CAR("Машина"),
    CLOTHES("Одежда"),
    PETS("Питомцы"),
    COMMUNICATION("Связь"),
    SPORT("Спорт"),
    ENTERTAINMENT("Развлечения"),
    OTHER("Прочее");

    fun toTransactionCategory(): TransactionCategory.Expense {
        return TransactionCategory.Expense(
            code = this.name,
            description = this.description
        )
    }
}

enum class IncomeCategory(
    val description: String
) {
    SALARY("Зарплата"),
    INVESTMENT("Инвестиции"),
    SAVINGS("Сбережения"),
    OTHER("Прочее");

    fun toTransactionCategory(): TransactionCategory.Income {
        return TransactionCategory.Income(
            code = this.name,
            description = this.description
        )
    }
}