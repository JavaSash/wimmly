package ru.telegram.bot.adapter.repository

import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import ru.telegram.bot.adapter.PostgresTestContainer
import ru.telegram.bot.adapter.TestConstants.Chat.TEXT
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.domain.tables.tables.ChatContext.Companion.CHAT_CONTEXT
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.strategy.dto.BotErrors

/**
 * Up only DataSource, jOOQ DSLContext, transaction manager for test
 */
@JooqTest
@Import(ChatContextRepository::class)
class ChatContextRepositoryTest : PostgresTestContainer() {
    @Autowired
    lateinit var chatCtxRepo: ChatContextRepository

    @Autowired
    lateinit var dsl: DSLContext

    @BeforeEach
    fun clean() {
        dsl.deleteFrom(CHAT_CONTEXT).execute()
    }

    @ParameterizedTest
    @ValueSource(longs = [999L, -123L])
    fun `isUserExist should return false when user does not exist`(id: Long) {
        assertFalse(chatCtxRepo.isUserExist(id))
    }

    @Test
    fun `isUserExist should return true when user exists`() {
        val user = chatCtxRepo.createUser(CHAT_ID)
        assertTrue(chatCtxRepo.isUserExist(user.id!!))
    }

    @Test
    fun `should create user`() {
        val user = chatCtxRepo.createUser(CHAT_ID)

        assertAll(
            { assertEquals(CHAT_ID, user.id) },
            { assertEquals(StepCode.START.toString(), user.stepCode) },
        )
    }

    @Test
    fun `getUser should return user`() {
        chatCtxRepo.createUser(CHAT_ID)

        val user = chatCtxRepo.getUser(CHAT_ID)

        assertAll(
            { assertNotNull(user) },
            { assertEquals(CHAT_ID, user!!.id) },
        )
    }

    @Test
    fun `should update step`() {
        chatCtxRepo.createUser(CHAT_ID)

        val updated = chatCtxRepo.updateUserStep(CHAT_ID, StepCode.START)

        assertEquals(StepCode.START.toString(), updated.stepCode)
    }

    @Test
    fun `should clear dialog state`() {
        chatCtxRepo.createUser(CHAT_ID)
        chatCtxRepo.updateUserStep(CHAT_ID, StepCode.CREATE_TRANSACTION)
        chatCtxRepo.updateText(CHAT_ID, TEXT)
        chatCtxRepo.updateAccept(CHAT_ID, true)
        chatCtxRepo.updateFlowContext(CHAT_ID, StepCode.CREATE_TRANSACTION.name)

        chatCtxRepo.clearDialogState(CHAT_ID)

        val user = chatCtxRepo.getUser(CHAT_ID)!!
        assertAll(
            { assertNull(user.stepCode) },
            { assertNull(user.text) },
            { assertNull(user.accept) },
            { assertNull(user.flowContext) },
        )
    }

    @Test
    fun `should update text`() {
        chatCtxRepo.createUser(CHAT_ID)

        chatCtxRepo.updateText(CHAT_ID, TEXT)

        val user = chatCtxRepo.getUser(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, user.id) },
            { assertEquals(TEXT, user.text) }
        )
    }

    @Test
    fun `should update error message and step`() {
        val errMsg = BotErrors.INVALID_AMOUNT.msg
        chatCtxRepo.createUser(CHAT_ID)

        chatCtxRepo.updateErrorMsgAndErrorStep(
            CHAT_ID,
            errMsg,
            StepCode.START
        )

        val user = chatCtxRepo.getUser(CHAT_ID)!!

        assertAll(
            { assertEquals(errMsg, user.errorMsg) },
            { assertEquals(StepCode.START.toString(), user.errorStep) }
        )
    }

    @Test
    fun `should update flow context`() {
        val flow = StepCode.CREATE_TRANSACTION.name
        chatCtxRepo.createUser(CHAT_ID)

        chatCtxRepo.updateFlowContext(CHAT_ID, flow)

        val user = chatCtxRepo.getUser(CHAT_ID)!!

        assertAll(
            { assertEquals(CHAT_ID, user.id) },
            { assertEquals(flow, user.flowContext) }
        )
    }

    @Test
    fun `should clear error data`() {
        chatCtxRepo.createUser(CHAT_ID)

        chatCtxRepo.updateErrorMsgAndErrorStep(
            CHAT_ID,
            BotErrors.INVALID_AMOUNT.msg,
            StepCode.START
        )

        chatCtxRepo.clearErrorData(CHAT_ID)

        val user = chatCtxRepo.getUser(CHAT_ID)!!

        assertAll(
            { assertNull(user.errorMsg) },
            { assertNull(user.errorStep) }
        )
    }
}