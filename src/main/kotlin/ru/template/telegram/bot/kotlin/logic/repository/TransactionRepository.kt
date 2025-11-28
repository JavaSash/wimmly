package ru.template.telegram.bot.kotlin.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.template.telegram.bot.kotlin.logic.model.entity.TransactionEntity
import java.util.UUID

interface TransactionRepository: JpaRepository<TransactionEntity, UUID> {

    fun findAllByUserId(userId: String): List<TransactionEntity>
}