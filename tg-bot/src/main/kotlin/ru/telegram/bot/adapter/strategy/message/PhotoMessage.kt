package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.component.MessageWriter
import ru.telegram.bot.adapter.service.FileService
import ru.telegram.bot.adapter.strategy.dto.PhotoDto
import java.io.ByteArrayInputStream

@Component
class PhotoMessage(
    messageWriter: MessageWriter,
    private val fileService: FileService
) : AbstractSendPhoto<PhotoDto>(messageWriter) {

    override fun file(data: PhotoDto?): ByteArrayInputStream? {
        return data?.url?.let {
            fileService.getFileFromUrl(data.url)
        }
    }

}