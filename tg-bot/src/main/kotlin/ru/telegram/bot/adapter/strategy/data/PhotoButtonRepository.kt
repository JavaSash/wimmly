package ru.telegram.bot.adapter.strategy.data

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.strategy.dto.PhotoButtonDto

@Repository
class PhotoButtonRepository : AbstractRepository<PhotoButtonDto>() {

    override fun getData(chatId: Long): PhotoButtonDto {
        return PhotoButtonDto(
            chatId,
            "https://api.dub.co/qr?url=google.com",
            listOf("test1", "test2")
        )
    }
}