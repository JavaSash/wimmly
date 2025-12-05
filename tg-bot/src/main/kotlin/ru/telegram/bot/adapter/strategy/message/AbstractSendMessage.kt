package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.dto.ReplyMarkupDto
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode

@Component
abstract class AbstractSendMessage<T: DataModel?>(private val messageWriter: MessageWriter) {

    fun classStepCode() = this.currentStepCode("Message")

    fun message(data: T? = null): String = messageWriter.process(classStepCode(), data)

    fun inlineButtons(chatId: Long, data: T?): List<MarkupDataDto> = emptyList()

    fun replyButtons(chatId: Long, data: T? = null): List<ReplyMarkupDto> = emptyList()

    fun isPermitted(chatId: Long): Boolean = true
}
