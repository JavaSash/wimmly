package ru.telegram.bot.adapter.utils

import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.utils.Constants.Transaction.MAX_AMOUNT
import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.formatMoney(): String {
    return "${this.setScale(2, RoundingMode.HALF_UP)} ₽"
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

    val value: BigDecimal = runCatching {
         normalized.toBigDecimal()
    }.getOrNull() ?: throw InvalidAmountException(BotErrors.INVALID_AMOUNT.msg)

    if (value <= BigDecimal.ZERO) throw InvalidAmountException(BotErrors.AMOUNT_SHOULD_BE_POSITIVE.msg)
    if (value > MAX_AMOUNT.toBigDecimal()) throw InvalidAmountException(BotErrors.AMOUNT_SHOULD_BE_SMALLER.msg)

    return value.setScale(2, RoundingMode.HALF_UP)
}