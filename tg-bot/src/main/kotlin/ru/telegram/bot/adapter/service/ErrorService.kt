package ru.telegram.bot.adapter.service

import mu.KLogging
import org.springframework.stereotype.Service
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.exceptions.InvalidDateException
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.dto.BotErrors

@Service
class ErrorService(private val chatContextRepository: ChatContextRepository) {

    companion object : KLogging()

    fun logError(chatId: Long, exc: Throwable, data: String?, errorStep: StepCode) {
        val errMsg = formErrorMsg(exc)
        logger.error { "$$$ [ErrorService] Error on step $errorStep with cause: $errMsg and type ${exc.javaClass} for input: $data " }
        chatContextRepository.updateErrorMsgAndErrorStep(
            chatId = chatId,
            errorMsg = errMsg,
            errorStep = errorStep // Чтобы вернуться на шаг возникновения ошибки после отправки инфы по ошибке пользователю
        )
    }

    fun getStepBeforeError(chatId: Long): StepCode {
        val errorStep = chatContextRepository.getUser(chatId)?.errorStep
        if (errorStep == null) logger.error { "$$$ Step before error is null" }
        logger.info { "$$$ [ErrorService] Before error step is: $errorStep" }
        chatContextRepository.clearErrorData(chatId)
        return StepCode.valueOf(errorStep!!)
    }

    private fun formErrorMsg(exc: Throwable) = when (exc) {
        is InvalidDateException, is InvalidAmountException -> exc.message!!
        else -> BotErrors.UNKNOWN.msg
    }
}