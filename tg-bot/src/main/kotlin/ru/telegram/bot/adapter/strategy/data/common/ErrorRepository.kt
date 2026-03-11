package ru.telegram.bot.adapter.strategy.data.common

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.strategy.dto.ErrorDto

/**
 * Data provider
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 * T should extend from DataModel
 */
@Repository
class ErrorRepository(
    private val chatContextRepository: ChatContextRepository
) : AbstractRepository<ErrorDto>() {
    companion object : KLogging()

    override fun getData(chatId: Long): ErrorDto = ErrorDto(chatContextRepository.getUser(chatId)?.errorMsg!!)
        .also { logger.info { "$$$ Error data for chat: $chatId is ${it.errorMsg}" } }
}