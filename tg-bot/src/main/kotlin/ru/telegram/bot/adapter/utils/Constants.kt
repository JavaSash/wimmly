package ru.telegram.bot.adapter.utils

import java.time.format.DateTimeFormatter

object Constants {

    object Transaction {
        const val INCOME = "INCOME"
        const val EXPENSE = "EXPENSE"
        val DATE_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val INTERVAL_PATTERN = Regex("""(\d{2}\.\d{2}\.\d{4})-(\d{2}\.\d{2}\.\d{4})""")
    }

    object Button {
        const val YES = "YES"
        const val NO = "NO"
    }
}