package ru.telegram.bot.adapter.strategy.data

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.strategy.dto.ButtonRequestDto

@Repository
class ButtonRequestRepository:  AbstractRepository<ButtonRequestDto>() {

    override fun getData(chatId: Long): ButtonRequestDto {
        return ButtonRequestDto(listOf("YES", "NO"))
    }
}