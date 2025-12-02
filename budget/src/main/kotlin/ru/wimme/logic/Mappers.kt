package ru.wimme.logic

import java.math.BigDecimal
import java.math.RoundingMode

fun Double.money(): BigDecimal =
    BigDecimal.valueOf(this).setScale(2, RoundingMode.HALF_UP)