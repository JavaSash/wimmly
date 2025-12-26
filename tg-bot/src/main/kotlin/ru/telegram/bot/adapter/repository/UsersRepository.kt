package ru.telegram.bot.adapter.repository

import mu.KLogging
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.domain.tables.Users.Companion.USERS
import ru.telegram.bot.adapter.domain.tables.pojos.Users
import ru.telegram.bot.adapter.dto.enums.StepCode
import java.math.BigDecimal
import java.time.LocalDate

@Repository
class UsersRepository(private val dslContext: DSLContext) {

    companion object : KLogging()

    // Проверка на существование пользователя в базе. Нужно 1 раз для команды /start
    fun isUserExist(chatId: Long): Boolean {
        return dslContext.selectCount().from(USERS).where(USERS.ID.eq(chatId)).fetchOneInto(Int::class.java) == 1
    }

    // Создание пользователя для команды /start
    fun createUser(chatId: Long): Users {
        val record = dslContext.newRecord(
            USERS, Users(
                id = chatId,
                stepCode = StepCode.START.toString()
            )
        )
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

    fun updateCategory(chatId: Long, category: String) {
        dslContext.update(USERS)
            .set(USERS.CATEGORY, category)
            .where(USERS.ID.eq(chatId)).execute()
    }

    // Обновление данных пришедших от кнопок
    fun updateAccept(chatId: Long, accept: Boolean) {
        dslContext.update(USERS)
            .set(USERS.ACCEPT, accept)
            .where(USERS.ID.eq(chatId)).execute()
    }

    fun updateTransactionType(chatId: Long, txType: String) {
        dslContext.update(USERS)
            .set(USERS.TRANSACTION_TYPE, txType)
            .where(USERS.ID.eq(chatId)).execute()
    }

    fun updateTransactionDate(chatId: Long, date: LocalDate) {
        dslContext.update(USERS)
            .set(USERS.TRANSACTION_DATE, date)
            .where(USERS.ID.eq(chatId)).execute()
    }

    fun updateAmount(chatId: Long, amount: String) {
        logger.info { "$$$ Updating amount for chat $chatId: $amount" }
        val bigDecimal = try {
            BigDecimal(amount)
        } catch (e: Exception) {
            logger.error("Failed to parse amount: $amount", e)
            BigDecimal.ZERO
        }

        dslContext.update(USERS)
            .set(USERS.AMOUNT, bigDecimal)
            .where(USERS.ID.eq(chatId)).execute()
    }

    fun updateComment(chatId: Long, comment: String?) {
        dslContext.update(USERS)
            .set(USERS.COMMENT, comment)
            .where(USERS.ID.eq(chatId)).execute()
    }

    fun updateText(chatId: Long, txt: String) {
        dslContext.update(USERS)
            .set(USERS.TEXT, txt)
            .where(USERS.ID.eq(chatId)).execute()
    }
}