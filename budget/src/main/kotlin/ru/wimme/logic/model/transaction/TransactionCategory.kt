package ru.wimme.logic.model.transaction

enum class TransactionCategory(
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