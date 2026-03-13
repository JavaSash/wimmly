package ru.telegram.bot.adapter.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import ru.telegram.bot.adapter.DbBasicTest
import ru.telegram.bot.adapter.TestConstants.Chat.TEXT
import ru.telegram.bot.adapter.TestConstants.Tx.INVALID_AMOUNT_VALUE
import ru.telegram.bot.adapter.TestConstants.Tx.INVALID_DATE_VALUE
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.exceptions.InvalidDateException
import ru.telegram.bot.adapter.strategy.dto.BotErrors

@Import(ErrorService::class)
class ErrorServiceTest: DbBasicTest() {

    @Autowired
    lateinit var errorService: ErrorService

    @BeforeEach
    fun setUp() {
        chatCtxRepo.createUser(CHAT_ID)
    }

    @Test
    fun `logError should save amount validation error`() {
        val exc = InvalidAmountException(BotErrors.INVALID_AMOUNT.msg)

        errorService.logError(
            chatId = CHAT_ID,
            exc = exc,
            data = INVALID_AMOUNT_VALUE,
            errorStep = StepCode.CREATE_TRANSACTION
        )

        val user = chatCtxRepo.getUser(CHAT_ID)!!
        assertAll(
            { assertEquals(exc.message, user.errorMsg) },
            { assertEquals(StepCode.CREATE_TRANSACTION.toString(), user.errorStep) }
        )
    }

    @Test
    fun `logError should save date validation error`() {
        val exc = InvalidDateException(BotErrors.INVALID_DATE.msg)

        errorService.logError(
            chatId = CHAT_ID,
            exc = exc,
            data = INVALID_DATE_VALUE,
            errorStep = StepCode.CREATE_TRANSACTION
        )

        val user = chatCtxRepo.getUser(CHAT_ID)!!
        assertAll(
            { assertEquals(exc.message, user.errorMsg) },
            { assertEquals(StepCode.CREATE_TRANSACTION.toString(), user.errorStep) }
        )
    }

    @Test
    fun `logError should save unknown error with generic message`() {
        val exc = RuntimeException("unexpected")

        errorService.logError(
            chatId = CHAT_ID,
            exc = exc,
            data = TEXT,
            errorStep = StepCode.CREATE_TRANSACTION
        )

        val user = chatCtxRepo.getUser(CHAT_ID)!!
        assertAll(
            { assertEquals(BotErrors.UNKNOWN.msg, user.errorMsg) },
            { assertEquals(StepCode.CREATE_TRANSACTION.toString(), user.errorStep) }
        )
    }

    @Test
    fun `getStepBeforeError should return step and clear error data`() {
        val errStep = StepCode.CREATE_TRANSACTION
        chatCtxRepo.updateErrorMsgAndErrorStep(
            CHAT_ID,
            BotErrors.INVALID_AMOUNT.msg,
            errStep
        )

        val step = errorService.getStepBeforeError(CHAT_ID)
        val user = chatCtxRepo.getUser(CHAT_ID)!!

        assertAll(
            { assertEquals(errStep, step) },
            { assertNull(user.errorMsg) },
            { assertNull(user.errorStep) }
        )
    }

    @Test
    fun `getStepBeforeError should log null and throw if no errorStep`() {
        chatCtxRepo.clearErrorData(CHAT_ID)

        assertNull(errorService.getStepBeforeError(CHAT_ID))
    }
}