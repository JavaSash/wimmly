package ru.template.telegram.bot.kotlin.logic.service

import org.springframework.stereotype.Service
import ru.template.telegram.bot.kotlin.logic.model.entity.UserEntity
import ru.template.telegram.bot.kotlin.logic.repository.UserRepository
import org.springframework.transaction.annotation.Transactional
import ru.template.telegram.bot.kotlin.logic.exception.NotFoundException

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun isRegistered(userId: String): Boolean =
        userRepository.existsById(userId)

    @Transactional
    fun register(userId: String): UserEntity {
        if (isRegistered(userId)) return userRepository.getReferenceById(userId)

        return userRepository.save(UserEntity(telegramId = userId))
    }

    fun getUser(userId: String): UserEntity =
        userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found: $userId") }
}