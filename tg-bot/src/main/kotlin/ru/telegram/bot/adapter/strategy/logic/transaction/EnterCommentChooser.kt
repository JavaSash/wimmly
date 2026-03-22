package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.strategy.dto.BotErrors
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.COMMENT_MAX_LENGTH
import ru.telegram.bot.adapter.utils.validateStringLength

@Component
class EnterCommentChooser(
    private val transactionDraftRepository: TransactionDraftRepository,
    private val errorService: ErrorService
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        val comment = message.text
        logger.info { "$$$ EnterCommentChooser.execute with params: \nchatId=$chatId\nmessage=$comment" }

        runCatching {
            comment.validateStringLength(
                maxLength = COMMENT_MAX_LENGTH,
                errorMsg = BotErrors.TOO_LONG_COMMENT.msg
            )
            transactionDraftRepository.updateComment(chatId, comment)
        }
            .onFailure { exc ->
                errorService.logError(chatId = chatId, exc = exc, data = comment, errorStep = StepCode.ENTER_COMMENT)
            }
    }
}