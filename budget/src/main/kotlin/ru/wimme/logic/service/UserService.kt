package ru.wimme.logic.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.wimme.logic.exception.NotFoundException
import ru.wimme.logic.model.entity.UserEntity
import ru.wimme.logic.model.user.UserRegistrationRq
import ru.wimme.logic.model.user.UserRegistrationRs
import ru.wimme.logic.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun isRegistered(userId: String): Boolean =
        userRepository.existsById(userId)

    @Transactional
    fun register(rq: UserRegistrationRq): UserRegistrationRs {
        if (isRegistered(rq.telegramUserId)) return UserRegistrationRs(userRepository.getReferenceById(rq.telegramUserId).tgId)

        return UserRegistrationRs(
            userRepository.save(
                UserEntity(
                    tgId = rq.telegramUserId,
                    name = rq.name
                )
            ).tgId
        )

    }

    fun getUser(userId: String): UserEntity =
        userRepository.findById(userId)
            .orElseThrow { NotFoundException("User not found: $userId") }
}