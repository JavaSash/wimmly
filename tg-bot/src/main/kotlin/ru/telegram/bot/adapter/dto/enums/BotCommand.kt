package ru.telegram.bot.adapter.dto.enums

/**
 * START("start", "Start work")
 * START - command
 * "start" - bot command in tg
 * "Start work" - text description
 */
enum class BotCommand(val command: String, val desc: String) {
    START("start", "Start work"),
    HELP("help", "Help"),
//    ADD_EXPENSE("add_expense", "Добавить расход"),
//    ADD_INCOME("add_income", "Добавить доход"),
    BALANCE("balance", "Текущий баланс"),
//    TRANSACTIONS("transactions", "Последние транзакции"),

    // not used
    USER_INFO("user_info", "user info"),
    BUTTON("button", "button yes no"),
    ACCESS("access", "access check"),
    CONTACT("contact", "contact check"),
    PHOTO("photo", "photo test"),
    PHOTO_BUTTON("photo_button", "photo test with button")
}
