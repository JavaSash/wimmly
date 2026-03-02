package ru.telegram.bot.adapter.dto.budget

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class TransactionRs(
    val id: UUID,
    val displayId: Long,
    val type: String,
    val userId: String,
    val category: String,
    val amount: BigDecimal,
    val comment: String?,
    val createdAt: Instant?
)
