package ru.telegram.bot.adapter.strategy.logic.transaction

import mu.KLogging
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class EnterAmountChooser(
    private val usersRepository: UsersRepository
) : MessageChooser {

    companion object : KLogging()

    override fun execute(chatId: Long, message: Message) {
        logger.info { "$$$ EnterAmountChooser.execute with params: \nchanId=$chatId\nmessage=$message" }
        val amountText = message.text?.trim()

        if (isValidAmount(amountText)) {
            usersRepository.updateAmount(chatId, amountText!!)
            usersRepository.updateUserStep(chatId, StepCode.ASK_DATE)
        } else {
            usersRepository.updateUserStep(chatId, StepCode.ENTER_AMOUNT)
        }
    }

    private fun isValidAmount(amount: String?): Boolean {
        return amount?.matches(Regex("\\d+(\\.\\d{1,2})?")) ?: false
    }
}