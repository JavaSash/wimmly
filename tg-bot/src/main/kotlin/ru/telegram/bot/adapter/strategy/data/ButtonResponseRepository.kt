package ru.telegram.bot.adapter.strategy.data

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.repository.UsersRepository
import ru.telegram.bot.adapter.strategy.dto.ButtonResponseDto

@Repository
class ButtonResponseRepository(private val usersRepository: UsersRepository) : AbstractRepository<ButtonResponseDto>() {

    override fun getData(chatId: Long): ButtonResponseDto {
        val user = usersRepository.getUser(chatId)!!
        return ButtonResponseDto(chatId, user.text, user.accept)
    }
}