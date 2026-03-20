package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Tx.TRX_ID
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.TransactionIdFormatException
import ru.telegram.bot.adapter.exceptions.TransactionIdNotExistException
import ru.telegram.bot.adapter.formMessage
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.utils.Constants.Transaction.MAX_TRX_ID

@ExtendWith(MockitoExtension::class)
class DeleteTransactionChooserTest {
    @Mock
    private lateinit var searchContextRepository: SearchContextRepository

    @Mock
    private lateinit var transactionService: TransactionService

    @Mock
    private lateinit var errorService: ErrorService

    private lateinit var chooser: DeleteTransactionChooser

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        chooser = DeleteTransactionChooser(
            searchContextRepository = searchContextRepository,
            transactionService = transactionService,
            errorService = errorService
        )
    }

    @ParameterizedTest
    @ValueSource(strings = [TRX_ID.toString(), "123 ", " 1", " 2 ", "$MAX_TRX_ID"])
    fun `execute should parse valid transaction id and update repository`(trxId: String) {
        val parsedId = trxId.trim().toLong()
        val message = formMessage(text = trxId)

        whenever(transactionService.isExist(chatId, parsedId)).thenReturn(true)

        chooser.execute(chatId, message)

        assertAll(
            { verify(searchContextRepository).updateTrxId(chatId, parsedId) },
            { verify(errorService, never()).logError(any(), any(), any(), any()) },
        )
    }

    @Test
    fun `execute should handle null message text`() {
        val message = formMessage(text = null)

        chooser.execute(chatId, message)

        assertAll(
            {
                verify(errorService).logError(
                    eq(chatId),
                    any<TransactionIdFormatException>(),
                    eq(null),
                    eq(StepCode.DELETE_TRANSACTION)
                )
            },
            { verify(searchContextRepository, never()).updateTrxId(any(), any()) },
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["abc", "!#@$", "", "  "])
    fun `execute should throw exc for non-numeric transaction id`(trxId: String) {
        val message = formMessage(text = trxId)

        chooser.execute(chatId, message)

        assertAll(
            {
                verify(errorService).logError(
                    eq(chatId),
                    any<TransactionIdFormatException>(),
                    eq(trxId.trim()),
                    eq(StepCode.DELETE_TRANSACTION)
                )
            },
            { verify(searchContextRepository, never()).updateTrxId(any(), any()) },
        )
    }

    @ParameterizedTest
    @ValueSource(strings = ["0", "2147483648", "-5", "-0"])
    fun `execute should throw exc for less than zero or more than max value`(trxId: String) {
        val message = formMessage(text = trxId)

        chooser.execute(chatId, message)

        assertAll(
            {
                verify(errorService).logError(
                    eq(chatId),
                    any<TransactionIdFormatException>(),
                    eq(trxId),
                    eq(StepCode.DELETE_TRANSACTION)
                )
            },
            { verify(searchContextRepository, never()).updateTrxId(any(), any()) },
        )
    }

    @Test
    fun `execute should handle non-existent transaction id`() {
        val trxId = TRX_ID
        val parsedId = TRX_ID.toString()
        val message = formMessage(text = parsedId)

        whenever(transactionService.isExist(chatId, trxId)).thenReturn(false)

        chooser.execute(chatId, message)

        verify(errorService).logError(
            eq(chatId),
            any<TransactionIdNotExistException>(),
            eq(parsedId),
            eq(StepCode.DELETE_TRANSACTION)
        )
        verify(searchContextRepository, never()).updateTrxId(any(), any())
    }

}