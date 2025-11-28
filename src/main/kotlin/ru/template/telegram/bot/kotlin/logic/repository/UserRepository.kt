package ru.template.telegram.bot.kotlin.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.template.telegram.bot.kotlin.logic.model.entity.UserEntity

interface UserRepository: JpaRepository<UserEntity, String> {
}