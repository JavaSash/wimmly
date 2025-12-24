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
    // Common
    START(StepType.SEND_MESSAGE, false),
    HELP(StepType.SEND_MESSAGE, false),
    FINAL(StepType.SEND_MESSAGE, false),
//    NOT_SUPPORTED(StepType.SEND_MESSAGE, false), // todo impl

    // Report
    BALANCE(StepType.SEND_MESSAGE, false),

    // Transaction
    ADD_INCOME(StepType.SEND_MESSAGE, false),
    ADD_EXPENSE(StepType.SEND_MESSAGE, false),
    SELECT_CATEGORY(StepType.SEND_MESSAGE, true), // с кнопками для выбора категории
    ENTER_AMOUNT(StepType.SEND_MESSAGE, true), // ожидаем ввода суммы
//    ENTER_COMMENT(StepType.SEND_MESSAGE, true), // ожидаем ввода комментария
//    CONFIRM_TRANSACTION(StepType.SEND_MESSAGE, false), // подтверждение с кнопками да/нет
//    SHOW_TRANSACTIONS(StepType.SEND_MESSAGE, false),

    // Просмотр
//    VIEW_TRANSACTIONS(StepType.INLINE_KEYBOARD_MARKUP, true), // просмотр с навигацией
//    CATEGORY_REPORT(StepType.SEND_MESSAGE, false), // отчет по категориям

    // Настройки
//    SETTINGS(StepType.INLINE_KEYBOARD_MARKUP, true),
//    EDIT_TRANSACTION(StepType.INLINE_KEYBOARD_MARKUP, true),
//    DELETE_TRANSACTION(StepType.INLINE_KEYBOARD_MARKUP, true),

    // not used
    BUTTON_RESPONSE(StepType.SEND_MESSAGE, true),
    ACCESS(StepType.SEND_MESSAGE, true),
    PHOTO(StepType.SEND_PHOTO, true),
    PHOTO_BUTTON(StepType.SEND_PHOTO, true),
    AWAIT(StepType.SEND_MESSAGE, false),
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
    SEND_PHOTO,

    /**
     * Msg with keyboard
     */
    REPLY_KEYBOARD_MARKUP
}