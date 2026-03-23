package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.exceptions.InvalidDateException
import ru.telegram.bot.adapter.formMessage
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.ErrorService
import ru.telegram.bot.adapter.utils.Constants.Date.ZONE_OFFSET
import ru.telegram.bot.adapter.utils.Constants.Transaction.FLEXIBLE_DATE_FORMAT
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class EnterDateChooserTest {
    @Mock
    private lateinit var transactionDraftRepository: TransactionDraftRepository

    @Mock
    private lateinit var errorService: ErrorService

    private lateinit var chooser: EnterDateChooser

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        chooser = EnterDateChooser(
            transactionDraftRepository = transactionDraftRepository,
            errorService = errorService
        )
    }

    @Test
    fun `execute should parse valid date and update repository`() {
        val dateText = "1.1.2024"
        val expectedInstant = LocalDate.parse(dateText, FLEXIBLE_DATE_FORMAT)
            .atStartOfDay(ZONE_OFFSET).toInstant()
        val message = formMessage(text = dateText)

        chooser.execute(chatId, message)

        assertAll(
            { verify(transactionDraftRepository).updateTransactionDate(chatId, expectedInstant) },
            { verify(errorService, never()).logError(any(), any(), any(), any()) }
        )
    }

    @Test
    fun `execute should handle invalid date and log error`() {
        val dateText = "invalid-date"
        val message = formMessage(text = dateText)

        chooser.execute(chatId, message)

        assertAll(
            { verify(errorService).logError(eq(chatId), any<InvalidDateException>(), eq(dateText), eq(StepCode.ENTER_DATE)) },
            { verify(transactionDraftRepository, never()).updateTransactionDate(any(), any()) }
        )
    }
}