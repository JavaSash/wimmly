package ru.telegram.bot.adapter.event

import ru.telegram.bot.adapter.dto.enums.StepCode

class TgStepMessageEvent(
    /**
     * ChatId from bot
     */
    val chatId: Long,
    /**
     * Этап или шаг в боте (стартовый, выбор кнопки, сообщение пришедшее после кнопки и тд и тп).
     * Не путать с командами, так как в команде может быть несколько этапов
     */
    val stepCode: StepCode
)
