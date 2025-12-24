package ru.wimme.logic.model.transaction

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
}

enum class IncomeCategory(
    val description: String
) {
    SALARY("Зарплата"),
    INVESTMENT("Инвестиции"),
    SAVINGS("Сбережения"),
    OTHER("Прочее")
}