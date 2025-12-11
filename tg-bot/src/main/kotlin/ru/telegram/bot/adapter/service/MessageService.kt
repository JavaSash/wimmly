package ru.telegram.bot.adapter.service

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.dto.ReplyMarkupDto
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.dto.enums.StepType
import ru.telegram.bot.adapter.event.TgStepMessageEvent
import ru.telegram.bot.adapter.strategy.MessageContext
import ru.telegram.bot.adapter.strategy.StepContext
import ru.telegram.bot.adapter.strategy.dto.DataModel

/**
 * Формирует объект Телеграм АПИ сообщения и делает запрос на отправку в бота
 */
@Service
class MessageService(
    private val telegramClient: TelegramClient, // отправщик сообщения. В статье через TelegramSender
    private val messageContext: MessageContext<DataModel>, // Формирование текстовок сообщения
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val stepContext: StepContext // Выбор следующего этапа
) {

    fun sendMessageToBot(
        chatId: Long,
        stepCode: StepCode
    ) {
        when (stepCode.type) {
            StepType.SEND_MESSAGE,
            StepType.INLINE_KEYBOARD_MARKUP,
            StepType.REPLY_KEYBOARD_MARKUP -> telegramClient.execute(sendMessage(chatId, stepCode))
            StepType.SEND_PHOTO -> telegramClient.execute(sendPhoto(chatId, stepCode))
        }

        if (!stepCode.botPause && stepCode != StepCode.FINAL) { // если нет паузы, то формируем следующее сообщение
            applicationEventPublisher.publishEvent(
                TgStepMessageEvent(
                    chatId = chatId,
                    stepCode = stepContext.next(chatId, stepCode)!!
                )
            )
        }
    }

    private fun sendPhoto(chatId: Long, stepCode: StepCode): SendPhoto {
        val photoMessage =
            messageContext.getPhotoMessage(chatId, stepCode)
                ?: throw IllegalArgumentException("photo data is empty")

        val replyMarkup = photoMessage.inlineButtons
            .takeIf { it.isNotEmpty() }?.getInlineKeyboardMarkup()?: ReplyKeyboardRemove(true)

        return SendPhoto.builder()
            .chatId(chatId)
            .caption(photoMessage.message)
            .photo(InputFile(photoMessage.file, "file"))
            .replyMarkup(replyMarkup)
            .build()
    }
    // SendMessage - объект телеграм АПИ для отправки сообщения
    private fun sendMessage(chatId: Long, stepCode: StepCode): SendMessage {
        val message = messageContext.getMessage(chatId, stepCode)
            ?: throw IllegalStateException("message is null")
        // Отправляем в бота сообщение с кнопками
        val markup = message.inlineButtons.getInlineKeyboardMarkup()
            .takeIf { it.keyboard.isNotEmpty() }
            ?: message.replyButtons.takeIf { it.isNotEmpty() }?.getReplyMarkup()
            ?: ReplyKeyboardRemove(true)

        val sendMessage = SendMessage.builder()
            .chatId(chatId)
            .text(message.message)
            .replyMarkup(markup)
            .build()

        sendMessage.enableHtml(true)

        return sendMessage
    }

    // Формируем модель кнопок
    private fun List<MarkupDataDto>.getInlineKeyboardMarkup(): InlineKeyboardMarkup {

        var inlineKeyboardButtonsInner: MutableList<InlineKeyboardButton>
        val inlineKeyboardButtons: MutableList<MutableList<InlineKeyboardButton>> = mutableListOf()

        this.groupBy { it.rowPos }.toSortedMap().forEach { entry: Map.Entry<Int, List<MarkupDataDto>> ->
            inlineKeyboardButtonsInner = mutableListOf()
            entry.value.forEach { markup: MarkupDataDto ->
                val button = InlineKeyboardButton(markup.text)
                button.callbackData = markup.text
                inlineKeyboardButtonsInner.add(button)
            }
            inlineKeyboardButtons.add(inlineKeyboardButtonsInner.toMutableList())
        }
        val rows = inlineKeyboardButtons.map { InlineKeyboardRow(it) }
        return InlineKeyboardMarkup(rows)
    }

    private fun List<ReplyMarkupDto>.getReplyMarkup(): ReplyKeyboard {
        val keyboardRows = this.map { rmd ->
            KeyboardRow(
                KeyboardButton.builder()
                    .text(rmd.text)
                    .requestContact(rmd.requestContact)
                    .build()
            )
        }
        return ReplyKeyboardMarkup.builder().keyboard(keyboardRows).build()
    }
}
