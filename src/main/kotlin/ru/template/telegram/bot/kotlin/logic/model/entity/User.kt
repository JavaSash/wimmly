package ru.template.telegram.bot.kotlin.logic.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "tg_id", nullable = false, updatable = false)
    val telegramId: String,
    @Column(name = "username")
    val username: String? = null,
    @Column(name = "language")
    val languageCode: String? = null,
) : BaseEntity()
