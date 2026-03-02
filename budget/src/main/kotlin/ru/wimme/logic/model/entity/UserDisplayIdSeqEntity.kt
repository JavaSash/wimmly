package ru.wimme.logic.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_display_id_seq")
data class UserDisplayIdSeqEntity(
    @Id
    @Column(name = "user_id")
    val userId: String,
    @Column(name = "current_seq")
    var currentSeq: Long
): BaseEntity()
