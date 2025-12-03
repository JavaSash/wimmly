package ru.template.telegram.bot.kotlin.template.dto.enums

/**
 * START("start", "Start work")
 * START - command
 * "start" - bot command in tg
 * "Start work" - text description
 */
enum class BotCommand(val command: String, val desc: String) {
    START("start", "Start work"),
    USER_INFO("user_info", "user info"),
    BUTTON("button", "button yes no"),
    ACCESS("access", "access check"),
    CONTACT("contact", "contact check"),
    PHOTO("photo", "photo test"),
    PHOTO_BUTTON("photo_button", "photo test with button")
}
