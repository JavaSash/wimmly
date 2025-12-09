package ru.telegram.bot.adapter.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.dto.DataModel
import ru.telegram.bot.adapter.strategy.logic.CallbackChooser
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.strategy.message.AbstractSendPhoto
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage

@Configuration
class TelegramConfig<T : DataModel>(
    private val botProperty: BotProperty,
    private val sendMessages: List<AbstractSendMessage<T>>,
    private val sendPhoto: List<AbstractSendPhoto<T>>,
    private val callbackChooser: List<CallbackChooser>,
    private val messageChooser: List<MessageChooser>
) {

    @Bean
    fun telegramClient(): TelegramClient {
        return OkHttpTelegramClient(botProperty.token)
    }

    @Bean
    fun sendMessages(): Map<StepCode, AbstractSendMessage<T>> {
        return sendMessages.associateBy { it.classStepCode() }
    }

    @Bean
    fun sendPhotos(): Map<StepCode, AbstractSendPhoto<T>> {
        return sendPhoto.associateBy { it.classStepCode() }
    }

    @Bean
    fun telegramMessageChooser(): Map<StepCode, MessageChooser> {
        return messageChooser.associateBy { it.classStepCode() }
    }

    @Bean
    fun telegramCallbackChooser(): Map<StepCode, CallbackChooser> {
        return callbackChooser.associateBy { it.classStepCode() }
    }

}