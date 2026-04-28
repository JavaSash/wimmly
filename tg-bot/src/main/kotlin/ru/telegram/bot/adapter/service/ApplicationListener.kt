package ru.telegram.bot.adapter.service

import mu.KLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.event.TgReceivedCallbackEvent
import ru.telegram.bot.adapter.event.TgReceivedMessageEvent
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.LogicContext
import ru.telegram.bot.adapter.strategy.StepContext

/**
 * Центральный оркестратор адаптера
 */
@Component
class ApplicationListener(
    private val logicContext: LogicContext, // Основная бизнес логика
    private val stepContext: StepContext, // Выбор следующего этапа
    private val chatContextRepository: ChatContextRepository, // Слой СУБД
    private val messageService: MessageService // Сервис, который формирует объект для отправки сообщения в бота
) {

    companion object : KLogging()

    inner class Message {
        /**
         * Publish [TgStepMessageEvent] if next step is available
         * @param event consumes data from [ReceiverService]
         * Consumes [TgReceivedMessageEvent]
         */
        @EventListener
        fun onApplicationEvent(event: TgReceivedMessageEvent) {
            logger.info { "$$$ ApplicationListener TgReceivedMessageEvent: $event" }
            logicContext.execute(chatId = event.chatId, message = event.message, stepCode = event.stepCode)
            val nextStepCode = stepContext.next(event.chatId, event.stepCode)
            if (nextStepCode != null) {
                stepMessageBean().onApplicationEvent(
                    TgStepMessageEvent(
                        chatId = event.chatId,
                        stepCode = nextStepCode
                    )
                )
            }
        }
    }

    inner class StepMessage {
        /**
         * Single point for change user dialogue step
         * Send msg to tg-bot
         * @param event consumes data from [ru.telegram.bot.adapter.strategy.command.common.AbstractCommand], [MessageService], [Message], [CallbackMessage]
         * Consumes [TgStepMessageEvent]
         */
        @EventListener
        fun onApplicationEvent(event: TgStepMessageEvent) {
            logger.info { "$$$ ApplicationListener.StepMessage TgStepMessageEvent data: \nchantId: ${event.chatId}\n stepCode:${event.stepCode}" }
            // Обновляем шаг в бд (тут, а не в Step\Chooser классах)
            chatContextRepository.updateUserStep(event.chatId, event.stepCode)
            // Отправляем сообщение в бота (и формируем)
            messageService.sendMessageToBot(event.chatId, event.stepCode)
        }
    }


    inner class CallbackMessage {
        /**
         * Publish [TgStepMessageEvent] if next step is available
         * @param event consumes data from [ReceiverService]
         * Consumes [TgReceivedCallbackEvent]
         */
        @EventListener
        fun onApplicationEvent(event: TgReceivedCallbackEvent) {
            logger.info { "$$$ ApplicationListener.CallbackMessage TgReceivedCallbackEvent: $event" }
            val nextStepCode = when (logicContext.execute(event.chatId, event.callback, event.stepCode)) {
                ExecuteStatus.FINAL -> { // Если бизнес процесс одобрил переход на новый этап
                    stepContext.next(event.chatId, event.stepCode)
                }
                // В эту ветку не должны попасть никогда (по логике сда идём когда несуществующую кнопку выбрали или можем вернуть этот статус в Chooser-классе
                ExecuteStatus.NOTHING -> throw IllegalStateException("Не поддерживается")
            }
            if (nextStepCode != null) {
                // редирект на событие TelegramStepMessageEvent
                stepMessageBean().onApplicationEvent(
                    TgStepMessageEvent(
                        chatId = event.chatId,
                        stepCode = nextStepCode
                    )
                )
            }
        }
    }

    /**
     * Бин поступления сообщения от пользователя
     */
    @Bean
    @Lazy
    fun messageBean(): Message = Message()

    /**
     * Бин отправки сообщения ботом
     */
    @Bean
    @Lazy
    fun stepMessageBean(): StepMessage = StepMessage()

    /**
     * Бин, который срабатывает в момент клика по кнопке
     */
    @Bean
    @Lazy
    fun callbackMessageBean(): CallbackMessage = CallbackMessage()

}