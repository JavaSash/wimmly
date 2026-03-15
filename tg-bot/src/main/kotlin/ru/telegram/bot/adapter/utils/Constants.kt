package ru.telegram.bot.adapter.utils

import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object Constants {

    object Transaction {
        const val INCOME = "INCOME"
        const val EXPENSE = "EXPENSE"
        const val COMMENT_MAX_LENGTH = 50
        const val MAX_AMOUNT = Long.MAX_VALUE
        val DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val FLEXIBLE_DATE_FORMAT: DateTimeFormatter =
            DateTimeFormatter.ofPattern("d.M.uuuu")
                .withResolverStyle(ResolverStyle.STRICT)
        val INTERVAL_PATTERN = Regex("""(\d{2}\.\d{2}\.\d{4})-(\d{2}\.\d{2}\.\d{4})""")
    }

    object Button {
        const val YES = "YES"
        const val NO = "NO"
    }

    object Errors {
        const val FUTURE_DATE = "Дата не может быть в будущем"
        const val NOT_UNIX_DATE = "Дата должна быть после 1970 года"
        const val END_BEFORE_START_DATE = "Начальная дата должна быть раньше конечной"
    }
}