package ru.wimme.logic.model.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@MappedSuperclass
abstract class BaseEntity(
    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: Instant? = null
)