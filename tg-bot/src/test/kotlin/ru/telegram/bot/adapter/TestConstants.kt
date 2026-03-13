package ru.telegram.bot.adapter

import java.math.BigDecimal

object TestConstants {

    object User {
        const val CHAT_ID = 123L
    }

    object Tx {
        const val SALARY_CATEGORY = "SALARY"
        const val TRX_ID = 1L
        const val COMMENT = "test comment"
        val AMOUNT_100 = BigDecimal(100)
    }

    object Chat {
        const val TEXT = "hello"
    }
}