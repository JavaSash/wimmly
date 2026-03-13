package ru.telegram.bot.adapter.repository

import mu.KLogging
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.domain.tables.tables.ChatContext.Companion.CHAT_CONTEXT
import ru.telegram.bot.adapter.domain.tables.tables.pojos.ChatContext
import ru.telegram.bot.adapter.dto.enums.StepCode
/*
todo iml with upd all needed fields
 */
@Repository
class ChatContextRepository(private val dslContext: DSLContext) {

    companion object : KLogging()

    // Проверка на существование пользователя в базе. Нужно 1 раз для команды /start
    fun isUserExist(chatId: Long): Boolean {
        return dslContext.selectCount().from(CHAT_CONTEXT)
            .where(CHAT_CONTEXT.ID.eq(chatId)).fetchOneInto(Int::class.java) == 1
    }

    // Создание пользователя для команды /start
    fun createUser(chatId: Long): ChatContext {
        val record = dslContext.newRecord(
            CHAT_CONTEXT, ChatContext(
                id = chatId,
                stepCode = StepCode.START.toString()
            )
        )
        record.store()
        return record.into(ChatContext::class.java)
    }

    // получить информацию о пользователе
    fun getUser(chatId: Long): ChatContext? =
        dslContext.selectFrom(CHAT_CONTEXT).where(CHAT_CONTEXT.ID.eq(chatId)).fetchOneInto(ChatContext::class.java)

    // Обновление этапа в боте
    fun updateUserStep(chatId: Long, stepCode: StepCode): ChatContext =
        dslContext.update(CHAT_CONTEXT)
            .set(CHAT_CONTEXT.STEP_CODE, stepCode.toString())
            .where(CHAT_CONTEXT.ID.eq(chatId)).returning().fetchOne()!!.into(ChatContext::class.java)

    // Обновление данных пришедших от кнопок
    fun updateAccept(chatId: Long, accept: Boolean) {
        dslContext.update(CHAT_CONTEXT)
            .set(CHAT_CONTEXT.ACCEPT, accept)
            .where(CHAT_CONTEXT.ID.eq(chatId)).execute()
    }

    fun updateText(chatId: Long, txt: String) {
        dslContext.update(CHAT_CONTEXT)
            .set(CHAT_CONTEXT.TEXT, txt)
            .where(CHAT_CONTEXT.ID.eq(chatId)).execute()
    }

    fun updateErrorMsgAndErrorStep(chatId: Long, errorMsg: String, errorStep: StepCode) {
        dslContext.update(CHAT_CONTEXT)
            .set(CHAT_CONTEXT.ERROR_MSG, errorMsg)
            .set(CHAT_CONTEXT.ERROR_STEP, errorStep.toString())
            .where(CHAT_CONTEXT.ID.eq(chatId)).execute()
    }

    fun updateFlowContext(chatId: Long, flow: String) {
        dslContext.update(CHAT_CONTEXT)
            .set(CHAT_CONTEXT.FLOW_CONTEXT, flow)
            .where(CHAT_CONTEXT.ID.eq(chatId)).execute()
    }

    fun clearDialogState(chatId: Long) {
        dslContext.update(CHAT_CONTEXT)
            .setNull(CHAT_CONTEXT.STEP_CODE)
            .setNull(CHAT_CONTEXT.TEXT)
            .setNull(CHAT_CONTEXT.ACCEPT)
            .setNull(CHAT_CONTEXT.FLOW_CONTEXT)
            .where(CHAT_CONTEXT.ID.eq(chatId))
            .execute()
    }

    fun clearErrorData(chatId: Long) {
        dslContext.update(CHAT_CONTEXT)
            .setNull(CHAT_CONTEXT.ERROR_MSG)
            .setNull(CHAT_CONTEXT.ERROR_STEP)
            .where(CHAT_CONTEXT.ID.eq(chatId))
            .execute()
    }
}