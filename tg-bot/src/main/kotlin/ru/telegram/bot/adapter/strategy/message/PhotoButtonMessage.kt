package ru.telegram.bot.adapter.strategy.message

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.dto.MarkupDataDto
import ru.telegram.bot.adapter.service.FileService
import ru.telegram.bot.adapter.strategy.dto.PhotoButtonDto
import java.io.ByteArrayInputStream

@Component
class PhotoButtonMessage(
    messageWriter: MessageWriter,
    private val fileService: FileService
) : AbstractSendPhoto<PhotoButtonDto>(messageWriter) {

    override fun file(data: PhotoButtonDto?): ByteArrayInputStream? {
        return data?.url?.let { fileService.getFileFromUrl(it) }
    }

    override fun inlineButtons(
        chatId: Long,
        data: PhotoButtonDto?
    ): List<MarkupDataDto> {
        return data?.buttons?.map { MarkupDataDto(0, it) } ?: emptyList()
    }
}