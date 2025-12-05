package ru.telegram.bot.adapter.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.event.TgReceivedCallbackEvent
import ru.telegram.bot.adapter.event.TgReceivedMessageEvent
import ru.telegram.bot.adapter.repository.UsersRepository
/**
 * Принимает текст, введенный пользователем или мета информацию по кнопке
 * если Кнопка, то событие для кнопки,
 * если текст, то событие для обработки текста.
 */
@Service
class ReceiverService(
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val usersRepository: UsersRepository
) {
    // выходной метод сервиса
    fun execute(update: Update) {
        if (update.hasCallbackQuery()) { // Выполнить, если это действие по кнопке
            callbackExecute(update.callbackQuery)
        } else if (update.hasMessage()) { // Выполнить, если это сообщение пользователя
            messageExecute(update.message)
        } else {
            throw IllegalStateException("Not yet supported")
        }
    }

    private fun messageExecute(message: Message) {
        val chatId = message.chatId
        val stepCode = usersRepository.getUser(chatId)!!.stepCode!! // Выбор текущего шага
        applicationEventPublisher.publishEvent( // Формируем событие TelegramReceivedMessageEvent
            TgReceivedMessageEvent(
                chatId = chatId,
                stepCode = StepCode.valueOf(stepCode),
                message = message
            )
        )
    }

    private fun callbackExecute(callback: CallbackQuery) {
        val chatId = callback.from.id
        val stepCode = usersRepository.getUser(chatId)!!.stepCode!! // Выбор текущего шага
        applicationEventPublisher.publishEvent( // Формируем событие TelegramReceivedCallbackEvent
            TgReceivedCallbackEvent(chatId = chatId, stepCode = StepCode.valueOf(stepCode), callback = callback)
        )
    }
}