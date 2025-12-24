package ru.telegram.bot.adapter.strategy.logic.report

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.message.Message
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser

@Component
class ReportTodayChooser(private val usersRepository: UsersRepository) : MessageChooser {

    override fun execute(chatId: Long, message: Message) {
        usersRepository.updateUserStep(chatId, StepCode.REPORT_TODAY)
    }
}