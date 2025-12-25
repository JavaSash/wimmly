package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.logic.common.CallbackChooser
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

@Component
class AskCommentChooser(
    private val usersRepository: UsersRepository
) : CallbackChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, callbackQuery: CallbackQuery): ExecuteStatus {
        logger.info { "$$$ AskCommentChooser.message data: ${callbackQuery.data}" }
        return when (callbackQuery.data) {
            YES -> {
                usersRepository.updateAccept(chatId, true)
                usersRepository.updateUserStep(chatId, StepCode.ENTER_COMMENT)
                ExecuteStatus.FINAL
            }

            NO -> {
                usersRepository.updateAccept(chatId, false)
                usersRepository.updateUserStep(chatId, StepCode.CREATE_TRANSACTION)
                ExecuteStatus.FINAL
            }

            else -> ExecuteStatus.NOTHING
        }
    }
}