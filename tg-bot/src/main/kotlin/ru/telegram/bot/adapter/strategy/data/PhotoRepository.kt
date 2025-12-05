package ru.telegram.bot.adapter.strategy.data

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.strategy.dto.PhotoDto

@Repository
class PhotoRepository: AbstractRepository<PhotoDto>() {

    override fun getData(chatId: Long): PhotoDto {
        return PhotoDto(chatId, "https://api.dub.co/qr?url=google.com")
    }
}