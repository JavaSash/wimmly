package ru.telegram.bot.adapter.strategy.logic.transaction

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
import ru.telegram.bot.adapter.utils.Constants.Transaction.DATE_PATTERN
import java.time.LocalDate
import java.time.ZoneOffset

@Component
class EnterDateChooser(
    private val usersRepository: UsersRepository
) : MessageChooser {
    override fun execute(chatId: Long, message: Message) {
        val dateText = message.text ?: ""

        runCatching {
            val date = LocalDate.parse(dateText, DATE_PATTERN)

            if (date.isAfter(LocalDate.now())) {
                throw IllegalArgumentException("Дата не может быть в будущем")
            }

            if (date.isBefore(LocalDate.EPOCH)) {
                throw IllegalArgumentException("Дата старее 1970г.")
            }

            usersRepository.updateTransactionDate(chatId, date.atStartOfDay(ZoneOffset.UTC).toInstant())
            usersRepository.updateAccept(chatId, false) // set to default to use in another buttons
            usersRepository.updateUserStep(chatId, StepCode.ASK_COMMENT)

        }.onFailure { error ->
            // При ошибке остаемся на том же шаге и показываем сообщение
            usersRepository.updateText(chatId, "Ошибка: ${error.message}\n\nПопробуйте снова:")
            // Остаемся на StepCode.ENTER_DATE для повторного ввода
        }
    }
}