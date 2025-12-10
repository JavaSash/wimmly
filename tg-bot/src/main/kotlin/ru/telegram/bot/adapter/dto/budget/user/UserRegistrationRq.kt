package ru.telegram.bot.adapter.dto.budget.user

data class UserRegistrationRq(
    val telegramUserId: String,
    val firstName: String,
    val userName: String?
)
