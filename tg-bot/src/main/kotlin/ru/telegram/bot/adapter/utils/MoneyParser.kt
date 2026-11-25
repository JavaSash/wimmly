package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.utils.Constants.Transaction.MAX_AMOUNT
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun BigDecimal.formatMoney(groupThousands: Boolean = false): String {
    val roundedAmount = setScale(2, RoundingMode.HALF_UP)
    val formattedAmount = if (groupThousands) {
        val symbols = DecimalFormatSymbols.getInstance(Locale.ROOT).apply {
            groupingSeparator = '\u00A0'
        }
        DecimalFormat("#,##0.00", symbols).format(roundedAmount)
    } else {
        roundedAmount.toString()
    }
    return "$formattedAmount ₽"
}

/**
 * @return normalized amount, throws [InvalidAmountException]
 * Need handle it and use [ru.telegram.bot.adapter.service.ErrorService.logError] to process error handle flow
 */
fun parseAmount(amount: String?): BigDecimal {
    if (amount.isNullOrBlank()) throw InvalidAmountException(BotErrors.INVALID_AMOUNT.msg)

    val normalized = amount
        .trim()
        .replace(" ", "")
        .replace(",", ".")

    val decimalPart = normalized.substringAfter('.', "")
    if (decimalPart.length > 2) throw InvalidAmountException(BotErrors.INVALID_AMOUNT.msg)

    val value: BigDecimal = runCatching {
         normalized.toBigDecimal()
    }.getOrNull() ?: throw InvalidAmountException(BotErrors.INVALID_AMOUNT.msg)

    if (value <= BigDecimal.ZERO) throw InvalidAmountException(BotErrors.AMOUNT_SHOULD_BE_POSITIVE.msg)
    if (value > MAX_AMOUNT.toBigDecimal()) throw InvalidAmountException(BotErrors.AMOUNT_SHOULD_BE_SMALLER.msg)

    return value.setScale(2, RoundingMode.HALF_UP)
}
