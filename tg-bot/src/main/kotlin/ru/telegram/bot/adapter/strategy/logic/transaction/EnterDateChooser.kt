package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.strategy.dto.formErrorMsg
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.FLEXIBLE_DATE_FORMAT
import ru.telegram.bot.adapter.utils.validateDate
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class EnterDateChooser(
    private val chatContextRepository: ChatContextRepository,
    private val transactionDraftRepository: TransactionDraftRepository,
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        val dateText = message.text?.trim() ?: ""

        runCatching {
            val date = LocalDate.parse(dateText, FLEXIBLE_DATE_FORMAT).atStartOfDay(ZoneOffset.UTC).toInstant()
            validateDate(date)
            transactionDraftRepository.updateTransactionDate(chatId, date)
        }.onFailure { exc ->
            val errMsg = formErrorMsg(exc)
            logger.error { "$$$ EnterDateChooser.execute error: $errMsg for input: $dateText " }
            chatContextRepository.updateErrorMsgAndErrorStep(
                chatId = chatId,
                errorMsg = errMsg,
                errorStep = StepCode.ENTER_DATE // Чтобы вернуться на шаг возникновения ошибки после отправки инфы по ошибке пользователю
            ) // todo вынести в отдельный класс ErrorResolver\ErrorHandler?

        }
    }
}