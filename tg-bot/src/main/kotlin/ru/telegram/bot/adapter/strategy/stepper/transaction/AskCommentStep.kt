package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.stepper.common.Step

@Component
class AskCommentStep(
    private val usersRepository: UsersRepository
) : Step {
    override fun getNextStep(chatId: Long): StepCode? {
        val user = usersRepository.getUser(chatId)
        return if (user?.accept == true) {
            StepCode.ENTER_COMMENT
        } else {
            StepCode.CREATE_TRANSACTION
        }
    }
}