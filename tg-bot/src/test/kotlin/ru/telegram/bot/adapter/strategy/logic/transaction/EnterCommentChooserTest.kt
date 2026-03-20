package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Tx.COMMENT
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.MaxLengthExceededException
import ru.telegram.bot.adapter.formMessage
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.utils.Constants.Transaction.COMMENT_MAX_LENGTH

@ExtendWith(MockitoExtension::class)
class EnterCommentChooserTest {
    @Mock
    private lateinit var transactionDraftRepository: TransactionDraftRepository

    @Mock
    private lateinit var errorService: ErrorService

    private lateinit var chooser: EnterCommentChooser

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        chooser = EnterCommentChooser(
            transactionDraftRepository = transactionDraftRepository,
            errorService = errorService
        )
    }

    @Test
    fun `execute should save valid comment and update repository`() {
        val comment = COMMENT
        val message = formMessage(text = comment)

        chooser.execute(chatId, message)

        assertAll(
            { verify(transactionDraftRepository).updateComment(chatId, comment) },
            { verify(errorService, never()).logError(any(), any(), any(), any()) }
        )
    }

    @Test
    fun `execute should handle too long comment and log error`() {
        val comment = "a".repeat(COMMENT_MAX_LENGTH + 1)
        val message = formMessage(text = comment)

        chooser.execute(chatId, message)

        assertAll(
            { verify(errorService).logError(eq(chatId), any<MaxLengthExceededException>(), eq(comment), eq(StepCode.ENTER_COMMENT)) },
            { verify(transactionDraftRepository, never()).updateComment(any(), any()) }
        )
    }
}