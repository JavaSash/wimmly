package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.INVALID_AMOUNT_VALUE
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.InvalidAmountException
import ru.telegram.bot.adapter.formMessage
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.ErrorService

@ExtendWith(MockitoExtension::class)
class EnterAmountChooserTest {
    @Mock
    private lateinit var transactionDraftRepository: TransactionDraftRepository

    @Mock
    private lateinit var errorService: ErrorService

    private lateinit var chooser: EnterAmountChooser

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        chooser = EnterAmountChooser(
            transactionDraftRepository = transactionDraftRepository,
            errorService = errorService
        )
    }

    @Test
    fun `execute should parse valid amount and update repository`() {
        val amount = AMOUNT_100
        val message = formMessage(amount.toString())

        chooser.execute(chatId, message)

        assertAll(
            { verify(transactionDraftRepository).updateAmount(chatId, amount.setScale(2)) },
            { verify(errorService, never()).logError(any(), any(), any(), any()) }
        )
    }

    @Test
    fun `execute should handle invalid amount and log error`() {
        val amount = INVALID_AMOUNT_VALUE
        val message = formMessage(amount)

        chooser.execute(chatId, message)

        assertAll(
            { verify(errorService).logError(eq(chatId), any<InvalidAmountException>(), eq(amount), eq(StepCode.ENTER_AMOUNT)) },
            { verify(transactionDraftRepository, never()).updateAmount(any(), any()) }
        )
    }
}