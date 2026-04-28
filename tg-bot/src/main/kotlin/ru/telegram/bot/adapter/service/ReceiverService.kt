package ru.telegram.bot.adapter.service

import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgReceivedCallbackEvent
import ru.telegram.bot.adapter.event.TgReceivedMessageEvent
import ru.telegram.bot.adapter.repository.ChatContextRepository

/**
 * Receive user's text or button meta info
 * If button - publish callback event
 * If text - publish msg event
 *
 * Event-Driven Architecture + State Machine
 */
@Service
class ReceiverService(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val chatContextRepository: ChatContextRepository
) {

    companion object : KLogging()

    /**
     * Main input method, defines update type
     * @param update - callbackQuery for button or message
     */
    fun execute(update: Update) {
        logger.info { "$$$ ReceiverService.execute with update: $update" }
        if (update.hasCallbackQuery()) { // button action
            callbackExecute(update.callbackQuery)
        } else if (update.hasMessage()) { // user msg
            messageExecute(update.message)
        } else {
            throw IllegalStateException("Not yet supported")
        }
    }

    /**
     * Define current step from chat ctx and publish event about chat info, current step and msg
     */
    private fun messageExecute(message: Message) {
        logger.info { "$$$ ReceiverService.messageExecute with msg: $message" }
        val chatId = message.chatId
        val stepCode = chatContextRepository.getUser(chatId)!!.stepCode!! // current step
        applicationEventPublisher.publishEvent(
            TgReceivedMessageEvent(
                chatId = chatId,
                stepCode = StepCode.valueOf(stepCode),
                message = message
            )
        )
    }

    /**
     * Define current step from chat ctx and publish event about chat info, current step and button callback
     */
    private fun callbackExecute(callback: CallbackQuery) {
        val chatId = callback.from.id
        val stepCode = chatContextRepository.getUser(chatId)!!.stepCode!! // current step
        applicationEventPublisher.publishEvent(
            TgReceivedCallbackEvent(
                chatId = chatId,
                stepCode = StepCode.valueOf(stepCode),
                callback = callback
            )
        )
    }
}