//package ru.telegram.bot.adapter.strategy.logic.transaction
//
//import org.springframework.stereotype.Component
//import org.telegram.telegrambots.meta.api.objects.message.Message
//import ru.telegram.bot.adapter.repository.UsersRepository
//import ru.telegram.bot.adapter.strategy.logic.common.MessageChooser
//
//@Component
//class AddExpenseChooser(private val usersRepository: UsersRepository) : MessageChooser {
//
//    override fun execute(chatId: Long, message: Message) {
//        // Сохраняем тип транзакции
//        usersRepository.updateTransactionType(chatId, "EXPENSE")
//    }
//}