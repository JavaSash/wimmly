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
    HELP(StepType.SEND_MESSAGE, false),
    FINAL(StepType.SEND_MESSAGE, false),
    // Команды
//    ADD_EXPENSE(StepType.SEND_MESSAGE, false),
//    ADD_INCOME(StepType.SEND_MESSAGE, false),
    BALANCE(StepType.SEND_MESSAGE, false),
    AWAIT(StepType.SEND_MESSAGE, false),
//    SHOW_TRANSACTIONS(StepType.SEND_MESSAGE, false),

    // Процесс добавления транзакции
    SELECT_CATEGORY(StepType.INLINE_KEYBOARD_MARKUP, true), // с кнопками для выбора категории
    ENTER_AMOUNT(StepType.SEND_MESSAGE, true), // ожидаем ввода суммы
    ENTER_COMMENT(StepType.SEND_MESSAGE, true), // ожидаем ввода комментария
    CONFIRM_TRANSACTION(StepType.INLINE_KEYBOARD_MARKUP, true), // подтверждение с кнопками да/нет

    // Просмотр
//    VIEW_TRANSACTIONS(StepType.INLINE_KEYBOARD_MARKUP, true), // просмотр с навигацией
//    CATEGORY_REPORT(StepType.SEND_MESSAGE, false), // отчет по категориям

    // Настройки
//    SETTINGS(StepType.INLINE_KEYBOARD_MARKUP, true),
//    EDIT_TRANSACTION(StepType.INLINE_KEYBOARD_MARKUP, true),
//    DELETE_TRANSACTION(StepType.INLINE_KEYBOARD_MARKUP, true),

    // not used
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
    SEND_PHOTO,

    /**
     * Msg with keyboard
     */
    REPLY_KEYBOARD_MARKUP
}