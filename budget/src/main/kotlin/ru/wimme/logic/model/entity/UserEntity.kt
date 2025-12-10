package ru.wimme.logic.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @Column(name = "tg_id", nullable = false, updatable = false)
    val tgId: String,
    @Column(name = "first_name")
    val firstName: String?,
    @Column(name = "name")
    val name: String?
) : BaseEntity()
