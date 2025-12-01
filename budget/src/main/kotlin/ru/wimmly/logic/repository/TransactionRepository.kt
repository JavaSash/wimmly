package ru.wimmly.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.wimmly.logic.model.entity.TransactionEntity
import java.util.*

@Repository
interface TransactionRepository: JpaRepository<TransactionEntity, UUID> {

    fun findAllByUserId(userId: String): List<TransactionEntity>
}