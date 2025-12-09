package ru.telegram.bot.adapter.strategy.command.transaction

//@Component
//class AddExpenseCommand(
//    private val usersRepository: UsersRepository,
//    private val applicationEventPublisher: ApplicationEventPublisher
//) : AbstractCommand(BotCommand.ADD_EXPENSE, usersRepository, applicationEventPublisher) {
//
//    override fun prepare(user: User, chat: Chat, arguments: Array<out String>) {
//        val chatId = chat.id
//        if (usersRepository.isUserExist(chatId)) {
//            usersRepository.updateUserStep(chatId, StepCode.ADD_EXPENSE)
//            // Сохраняем тип транзакции
//            usersRepository.updateTransactionType(chatId, "EXPENSE")
//        } else {
//            val newUser = usersRepository.createUser(chatId)
//            usersRepository.updateTransactionType(chatId, "EXPENSE")
//        }
//    }
//}