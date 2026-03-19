package ru.telegram.bot.adapter.strategy.data.transaction

import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.strategy.data.common.AskYesNoRepository

@Repository
class AskCommentRepository : AskYesNoRepository()