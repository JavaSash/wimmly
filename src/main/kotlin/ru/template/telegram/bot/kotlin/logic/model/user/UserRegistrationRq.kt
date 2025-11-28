package ru.template.telegram.bot.kotlin.logic.model.user

data class UserRegistrationRq(
    val telegramUserId: String,
    val name: String
)
