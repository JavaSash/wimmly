package ru.telegram.bot.adapter.strategy

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.MessageModelDto
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.data.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import ru.telegram.bot.adapter.strategy.message.AbstractSendPhoto

@Component
class MessageContext<T : DataModel>(
    private val sendMessages: Map<StepCode, AbstractSendMessage<T>>,
    private val sendPhotos: Map<StepCode, AbstractSendPhoto<T>>,
    private val abstractRepository: List<AbstractRepository<T>>
) {

    fun getMessage(chatId: Long, stepCode: StepCode): MessageModelDto? {
        return sendMessages[stepCode]
            ?.takeIf { it.isPermitted(chatId) }
            ?.let {
                val data = getData(chatId, stepCode)
                MessageModelDto(
                    message = it.message(data),
                    inlineButtons = it.inlineButtons(chatId, data),
                    replyButtons = it.replyButtons(chatId, data)
                )
            }
    }

    fun getPhotoMessage(chatId: Long, stepCode: StepCode): MessageModelDto? {
        return sendPhotos[stepCode]
            ?.takeIf { it.isPermitted(chatId) }
            ?.let {
                val data = getData(chatId, stepCode)
                MessageModelDto(
                    message = it.message(data),
                    inlineButtons = it.inlineButtons(chatId, data),
                    file = it.file(data)
                )
            }
    }

    private fun getData(chatId: Long, stepCode: StepCode): T? {
        return abstractRepository.firstOrNull { it.isAvailableForCurrentStep(stepCode) }?.getData(chatId)
    }
}
