package ru.telegram.bot.adapter.dto.budget

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
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
    val amountByCategory: Map<String, BigDecimal>
)
