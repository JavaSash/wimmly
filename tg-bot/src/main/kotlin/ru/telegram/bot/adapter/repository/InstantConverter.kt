package ru.telegram.bot.adapter.repository

import org.jooq.Converter
import ru.telegram.bot.adapter.utils.Constants.Date.ZONE_OFFSET
import java.time.Instant
import java.time.LocalDateTime

/**
 * timestamp SQL to java Instant (instead LDT)
 * Used when jooq generate POJOs (build gradle jooq config forcedTypes)
 */
class InstantConverter : Converter<LocalDateTime, Instant> {

    override fun from(databaseObject: LocalDateTime?): Instant? = databaseObject?.toInstant(ZONE_OFFSET)

    override fun to(userObject: Instant?): LocalDateTime? = userObject?.atOffset(ZONE_OFFSET)?.toLocalDateTime()

    override fun fromType(): Class<LocalDateTime> = LocalDateTime::class.java

    override fun toType(): Class<Instant> = Instant::class.java
}