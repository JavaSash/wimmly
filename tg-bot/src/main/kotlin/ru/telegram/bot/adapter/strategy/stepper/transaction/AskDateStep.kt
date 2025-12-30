package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step
import java.time.Instant

@Component
class AskDateStep(
    private val usersRepository: UsersRepository
) : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        val user = usersRepository.getUser(chatId)
        return if (user?.accept == true) {
            StepCode.ENTER_DATE
        } else {
            usersRepository.updateTransactionDate(chatId, Instant.now())
            StepCode.ASK_COMMENT
        }
    }
}