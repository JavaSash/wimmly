package ru.telegram.bot.adapter.strategy.data.transaction

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.strategy.data.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.AskYesNoDto
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

@Repository
class AskCommentRepository : AbstractRepository<AskYesNoDto>() {

    override fun getData(chatId: Long): AskYesNoDto {
        return AskYesNoDto(listOf(YES, NO))
    }
}