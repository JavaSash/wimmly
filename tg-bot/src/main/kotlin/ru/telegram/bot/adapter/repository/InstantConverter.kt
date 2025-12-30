package ru.telegram.bot.adapter.repository

import org.jooq.Converter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * timestamp SQL to java Instant (instead LDT)
 * Used when jooq generate POJOs (build gradle jooq config forcedTypes)
 */
class InstantConverter : Converter<LocalDateTime, Instant> {

    override fun from(databaseObject: LocalDateTime?): Instant? {
        return databaseObject?.atZone(ZoneId.systemDefault())?.toInstant()
    }

    override fun to(userObject: Instant?): LocalDateTime? {
        return userObject?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
    }

    override fun fromType(): Class<LocalDateTime> {
        return LocalDateTime::class.java
    }

    override fun toType(): Class<Instant> {
        return Instant::class.java
    }
}