package ru.template.telegram.bot.kotlin.logic.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.template.telegram.bot.kotlin.logic.model.entity.UserEntity

@Repository
interface UserRepository: JpaRepository<UserEntity, String> {
}