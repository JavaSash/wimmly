package ru.telegram.bot.adapter.utils

import java.time.format.DateTimeFormatter

object Constants {

    object Transaction {
        const val INCOME = "INCOME"
        const val EXPENSE = "EXPENSE"
        val DATE_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }

    object Button {
        const val YES = "YES"
        const val NO = "NO"
    }
}