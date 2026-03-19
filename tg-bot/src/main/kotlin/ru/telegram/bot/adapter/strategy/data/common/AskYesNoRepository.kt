package ru.telegram.bot.adapter.strategy.data.common

import ru.telegram.bot.adapter.strategy.dto.AskYesNoDto
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

abstract class AskYesNoRepository: AbstractRepository<AskYesNoDto>() {

    override fun getData(chatId: Long): AskYesNoDto {
        return AskYesNoDto(listOf(YES, NO))
    }
}