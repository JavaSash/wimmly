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
        val AMOUNT_0 = BigDecimal.ZERO
        val AMOUNT_100 = BigDecimal(100)
        val AMOUNT_50 = BigDecimal(50)
        val AMOUNT_150 = BigDecimal(150)
        const val INVALID_AMOUNT_VALUE = "f12"
        const val INVALID_DATE_VALUE = "2026-02-30"
    }

    object Chat {
        const val TEXT = "hello"
    }
}