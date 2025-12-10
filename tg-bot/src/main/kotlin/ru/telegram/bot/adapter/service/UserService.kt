package ru.telegram.bot.adapter.service

import mu.KLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.User
import ru.telegram.bot.adapter.client.UserClient
import ru.telegram.bot.adapter.dto.budget.user.UserRegistrationRq

@Service
class UserService(
    private val userClient: UserClient
) {
    companion object : KLogging()

    @Async("taskExecutor")
    fun syncUserToBackend(chatId: Long, tgUser: User) {
        try {
            userClient.registerUser(
                UserRegistrationRq(
                    telegramUserId = tgUser.id.toString(),
                    firstName = tgUser.firstName,
                    userName = tgUser.userName
                )
            ).also { logger.info { "$$$ User ${it.userId} registered" } }
        } catch (e: Exception) {
            logger.error("$$$ Failed to sync user $chatId: ${e.message}")
        }
    }
}