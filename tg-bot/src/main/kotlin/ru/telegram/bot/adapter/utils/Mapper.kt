package ru.telegram.bot.adapter.utils

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.formatMoney(): String {
    return "${this.setScale(2, RoundingMode.HALF_UP)} ₽"
}