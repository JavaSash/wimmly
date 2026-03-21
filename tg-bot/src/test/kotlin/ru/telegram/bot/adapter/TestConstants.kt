package ru.telegram.bot.adapter

import ru.telegram.bot.adapter.dto.budget.CategoryDto
import ru.telegram.bot.adapter.strategy.dto.AskYesNoDto
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME
import java.math.BigDecimal

object TestConstants {

    object Tx {
        const val SALARY_CATEGORY = "SALARY"
        const val INVESTMENT_CATEGORY = "INVESTMENT"
        const val FOOD_CATEGORY = "FOOD"
        const val HOME_CATEGORY = "HOME"
        val SALARY_CATEGORY_DTO = CategoryDto("SALARY", "Зарплата", INCOME)
        val FOOD_CATEGORY_DTO = CategoryDto("FOOD", "Еда", EXPENSE)
        const val TRX_ID = 1L
        const val COMMENT = "test comment"
        val AMOUNT_0 = BigDecimal.ZERO
        val AMOUNT_100 = BigDecimal(100)
        val AMOUNT_50 = BigDecimal(50)
        val AMOUNT_150 = BigDecimal(150)
        const val INVALID_AMOUNT_VALUE = "f12"
        const val INVALID_DATE_VALUE = "2026-02-30"
        const val FORMATTED_DATE = "01.01.2001"
        const val FORMATTED_MSG = "formatted message"
    }

    object Chat {
        const val TEXT = "hello"
        const val CHAT_ID = 123L
        const val UNKNOWN_FLOW = "UNKNOWN_FLOW"
    }

    object Button {
        val YES_NO_BUTTONS = AskYesNoDto(listOf(YES, NO))
    }
}