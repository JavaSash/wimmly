package ru.telegram.bot.adapter.strategy.data

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.strategy.dto.UserInfoDto

@Repository
class UserInfoRepository: AbstractRepository<UserInfoDto>() {

    override fun getData(chatId: Long): UserInfoDto {
        return UserInfoDto(chatId)
    }
}