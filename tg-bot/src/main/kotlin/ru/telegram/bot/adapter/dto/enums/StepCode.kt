package ru.telegram.bot.adapter.dto.enums

/**
 * носит информацию о типе сообщения, о шаге и прочую системную информацию
 * Тип (Простой текст или текст с кнопками) и botPause - остановить переход на новый этап для принятия решения пользователем
 * Когда мы выбираем ту или иную команду формируется сообщение, которое отправляется пользователю.
 * Иногда нужно отправить несколько сообщений подряд. Например START и затем USER_INFO.
 * botPause нужен в первую очередь, чтобы проинформировать пользователя о необходимости принятия решений.
 * Некоторые сообщения приходят с кнопками. Для этого и нужен енум StepType
 */
enum class StepCode(val type: StepType, val botPause: Boolean) {
    START(StepType.SEND_MESSAGE, false),
    USER_INFO(StepType.SEND_MESSAGE, true),
    BUTTON_REQUEST(StepType.SEND_MESSAGE, true),
    BUTTON_RESPONSE(StepType.SEND_MESSAGE, true),
    ACCESS(StepType.SEND_MESSAGE, true),
    PHOTO(StepType.SEND_PHOTO, true),
    PHOTO_BUTTON(StepType.SEND_PHOTO, true),
    CONTACT(StepType.SEND_MESSAGE, true)
}

enum class StepType {
    /**
     * Simple msg
     */
    SEND_MESSAGE,

    /**
     * Msg with button
     */
    INLINE_KEYBOARD_MARKUP,

    /**
     * Msg wit photo
     */
    SEND_PHOTO
}