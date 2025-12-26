package ru.wimme.logic.model.report

import ru.wimme.logic.model.transaction.TransactionCategory
import java.math.BigDecimal

data class PeriodReport(
    /**
     * Current balance
     */
    val balance: BigDecimal,
    val periodName: String,
    /**
     * Details by categories
     */
    val income: TxTypeDetail,
    /**
     * Details by categories
     */
    val expense: TxTypeDetail
)

/**
 * Income\expense details
 */
data class TxTypeDetail(
    /**
     * Income\expense amount for period
     */
    val txTypeAmount: BigDecimal,
    /**
     * Detailed info by categories
     * Key - category name (enum value)
     * value - sum for category
     */
    val amountByCategory: Map<TransactionCategory, BigDecimal>
)
