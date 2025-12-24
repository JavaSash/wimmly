package ru.telegram.bot.adapter.dto.enums

/**
 * START("start", "Start work")
 * START - command
 * "start" - bot command in tg
 * "Start work" - text description
 */
enum class BotCommand(val command: String, val desc: String) {
    // Common
    START("start", "Start work"),
    HELP("help", "Help"),

    //Report
    BALANCE("balance", "Текущий баланс"),
    REPORT_TODAY("report_today", "Баланс на сегодня"),

    // Transaction
    ADD_INCOME("add_income", "Добавить доход"),
    ADD_EXPENSE("add_expense", "Добавить расход"),
//    TRANSACTIONS("transactions", "Последние транзакции"),

    // not used
    ACCESS("access", "access check"),
    CONTACT("contact", "contact check"),
    PHOTO("photo", "photo test"),
    PHOTO_BUTTON("photo_button", "photo test with button")
}
