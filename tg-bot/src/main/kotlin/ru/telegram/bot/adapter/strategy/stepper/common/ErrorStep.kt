package ru.telegram.bot.adapter.strategy.stepper.common

import mu.KLogging
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository

@Component
class ErrorStep(
    private val chatContextRepository: ChatContextRepository
) : Step {

    companion object : KLogging()

    override fun getNextStep(chatId: Long): StepCode {
        val errorStep = chatContextRepository.getUser(chatId)?.errorStep
        if (errorStep == null) logger.error { "$$$ Error previous step is null" }
        logger.info { "$$$ ErrorStep return to previous step: $errorStep" }
        chatContextRepository.clearErrorData(chatId)
        return StepCode.valueOf(errorStep!!)
    }
}