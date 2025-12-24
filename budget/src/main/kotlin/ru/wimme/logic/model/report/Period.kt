package ru.wimme.logic.model.report

import java.time.Instant

data class Period(
    val from: Instant,
    val to: Instant,
)
