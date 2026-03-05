package ru.telegram.bot.adapter.strategy.data

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.dto.ButtonResponseDto

@Repository
class ButtonResponseRepository(private val chatContextRepository: ChatContextRepository) : AbstractRepository<ButtonResponseDto>() {

    override fun getData(chatId: Long): ButtonResponseDto {
        val user = chatContextRepository.getUser(chatId)!!
        return ButtonResponseDto(chatId, user.text, user.accept)
    }
}