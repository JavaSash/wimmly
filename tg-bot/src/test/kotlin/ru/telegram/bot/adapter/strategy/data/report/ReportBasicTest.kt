package ru.telegram.bot.adapter.strategy.data.report

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_150
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_50
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.client.ReportClient
import ru.telegram.bot.adapter.dto.budget.TxTypeDetail
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository.Companion.EXPENSE_CATEGORIES_STUB
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository.Companion.INCOME_CATEGORIES_STUB
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
abstract class ReportBasicTest {
    @Mock
    lateinit var reportClient: ReportClient

    protected val chatId = CHAT_ID

    protected fun formIncomeTxDetail(
        amount: BigDecimal = AMOUNT_150,
        amountByCategory: Map<String, BigDecimal> = mapOf(INCOME_CATEGORIES_STUB.first().code to amount)
    ) =
        TxTypeDetail(
            txTypeAmount = amount,
            amountByCategory = amountByCategory
        )

    protected fun formExpenseTxDetail(
        amount: BigDecimal = AMOUNT_50,
        amountByCategory: Map<String, BigDecimal> = mapOf(EXPENSE_CATEGORIES_STUB.first().code to amount)
    ) =
        TxTypeDetail(
            txTypeAmount = amount,
            amountByCategory = amountByCategory
        )
}