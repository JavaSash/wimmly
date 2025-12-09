package ru.telegram.bot.adapter.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.domain.tables.Users.Companion.USERS
import ru.telegram.bot.adapter.domain.tables.pojos.Users
import ru.telegram.bot.adapter.dto.enums.StepCode

@Repository
class UsersRepository(private val dslContext: DSLContext) {

    // Проверка на существование пользователя в базе. Нужно 1 раз для команды /start
    fun isUserExist(chatId: Long): Boolean {
        return dslContext.selectCount().from(USERS).where(USERS.ID.eq(chatId)).fetchOneInto(Int::class.java) == 1
    }

    // Создание пользователя для команды /start
    fun createUser(chatId: Long): Users {
        val record = dslContext.newRecord(USERS, Users(
            id = chatId,
            stepCode = StepCode.START.toString()
        ))
        record.store()
        return record.into(Users::class.java)
    }

    // получить информацию о пользователе
    fun getUser(chatId: Long) =
        dslContext.selectFrom(USERS).where(USERS.ID.eq(chatId)).fetchOneInto(Users::class.java)

    // Обновление этапа в боте
    fun updateUserStep(chatId: Long, stepCode: StepCode): Users =
        dslContext.update(USERS)
            .set(USERS.STEP_CODE, stepCode.toString())
            .where(USERS.ID.eq(chatId)).returning().fetchOne()!!.into(Users::class.java)

    // Обновление текста. Этот метод срабатывает у команды /user_info
    fun updateText(chatId: Long, text: String) {
        dslContext.update(USERS)
            .set(USERS.TEXT, text)
            .where(USERS.ID.eq(chatId)).execute()
    }

    // Обновление данных пришедших от кнопок в команде /button
    fun updateAccept(chatId: Long, accept: String) {
        dslContext.update(USERS)
            .set(USERS.ACCEPT, accept)
            .where(USERS.ID.eq(chatId)).execute()
    }

    fun updateTransactionType(chatId: Long, txType: String) {
        dslContext.update(USERS)
            .set(USERS.TRANSACTION_TYPE, txType)
            .where(USERS.ID.eq(chatId)).execute()
    }
}