package ru.telegram.bot.adapter

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.TestConstants.Chat.TEXT
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_150
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_50
import ru.telegram.bot.adapter.TestConstants.Tx.COMMENT
import ru.telegram.bot.adapter.TestConstants.Tx.FOOD_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.FORMATTED_DATE
import ru.telegram.bot.adapter.TestConstants.Tx.HOME_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.INVESTMENT_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.SALARY_CATEGORY
import ru.telegram.bot.adapter.TestConstants.Tx.TRX_ID
import ru.telegram.bot.adapter.domain.tables.tables.pojos.ChatContext
import ru.telegram.bot.adapter.domain.tables.tables.pojos.SearchContext
import ru.telegram.bot.adapter.domain.tables.tables.pojos.TransactionDraft
import ru.telegram.bot.adapter.dto.ReplyMarkupDto
import ru.telegram.bot.adapter.dto.budget.TransactionRs
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.dto.view.CategoryInfo
import ru.telegram.bot.adapter.strategy.dto.ReportDetail
import ru.telegram.bot.adapter.strategy.dto.ReportDto
import ru.telegram.bot.adapter.strategy.dto.ReportDto.Companion.TODAY
import ru.telegram.bot.adapter.strategy.dto.ShowTransactionsDto
import ru.telegram.bot.adapter.strategy.dto.TransactionItem
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME
import ru.telegram.bot.adapter.utils.formatMoney
import java.math.BigDecimal
import java.time.Instant
import java.util.*

fun formSearchContext(type: String = EXPENSE) =
    SearchContext(
        chatId = CHAT_ID,
        trxId = TRX_ID,
        type = type,
        category = FOOD_CATEGORY
    )

fun formTransactionRs(type: String = EXPENSE, category: String = FOOD_CATEGORY) =
    TransactionRs(
        id = UUID.randomUUID(),
        displayId = 1L,
        type = type,
        userId = CHAT_ID.toString(),
        category = category,
        amount = AMOUNT_100,
        comment = COMMENT,
        createdAt = Instant.now()
    )

fun formChatContext(flowContext: String? = StepCode.SEARCH_TRANSACTIONS.name, accept: Boolean? = null) =
    ChatContext(
        id = CHAT_ID,
        stepCode = null,
        text = null,
        accept = accept,
        flowContext = flowContext,
        errorMsg = null,
        errorStep = null
    )

fun formTransactionDraft(type: String = EXPENSE) =
    TransactionDraft(
        chatId = CHAT_ID,
        type = type,
        category = null,
        amount = null,
        date = null,
        comment = null
    )

fun formCallbackQuery(data: String) =
    CallbackQuery().apply {
        this.data = data
    }

fun formMessage(text: String? = null): Message =
    Message.builder()
        .text(text)
        .build()

fun formReportDto(): ReportDto {
    val income = formIncomeReportDetail()
    val expense = formExpenseReportDetail()
    return ReportDto(
        balance = income.amount - expense.amount,
        income = income,
        expense = expense,
        periodName = TODAY
    )
}

fun formReportDetail(amountByCategory: Map<String, BigDecimal>): ReportDetail = ReportDetail(
    amount = amountByCategory.values.sumOf { it },
    amountByCategory = amountByCategory
)


fun formIncomeReportDetail(totalAmount: BigDecimal = AMOUNT_150) =
    ReportDetail(
        amount = totalAmount,
        amountByCategory = mapOf(
            SALARY_CATEGORY to AMOUNT_100,
            INVESTMENT_CATEGORY to AMOUNT_50
        )
    )

fun formExpenseReportDetail(totalAmount: BigDecimal = AMOUNT_100) =
    ReportDetail(
        amount = totalAmount,
        amountByCategory = mapOf(
            FOOD_CATEGORY to AMOUNT_50,
            HOME_CATEGORY to AMOUNT_50
        )
    )

fun formCategoryInfo(
    categoryName: String = SALARY_CATEGORY,
    amount: BigDecimal = AMOUNT_100,
    isExpense: Boolean = false
) =
    CategoryInfo(
        name = categoryName,
        amount = amount,
        formattedAmount = amount.formatMoney(),
        percentage = "1",
        isExpense = isExpense
    )

fun formShowTransactionsDto(trx: List<TransactionItem>) = ShowTransactionsDto(trx)

fun formTransactionItem(amount: BigDecimal = AMOUNT_100) =
    TransactionItem(
        displayId = TRX_ID,
        formattedDate = FORMATTED_DATE,
        category = SALARY_CATEGORY,
        type = INCOME,
        amount = amount.toString(),
        comment = null
    )

fun formReplyMarkupDto() =
    ReplyMarkupDto(
        requestContact = false,
        text = TEXT
    )