package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formTransactionDraft
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.TransactionService

@ExtendWith(MockitoExtension::class)
class CreateTransactionStepTest {
    @Mock
    lateinit var transactionDraftRepository: TransactionDraftRepository
    @Mock
    lateinit var txService: TransactionService

    private lateinit var createTransactionStep: CreateTransactionStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        createTransactionStep = CreateTransactionStep(transactionDraftRepository, txService)
    }

    @Test
    fun `getNextStep should create transaction from draft and return BALANCE`() {
        val trxDraft = formTransactionDraft()
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(trxDraft)
        whenever(txService.addTransaction(trxDraft)).thenReturn("trx")

        val result = createTransactionStep.getNextStep(chatId)

        assertAll(
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
            { verify(txService).addTransaction(trxDraft) },
            { assertEquals(StepCode.BALANCE, result) }
        )
    }

    @Test
    fun `getNextStep should throw exception when transaction draft is null`() {
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(null)

        assertThrows<NullPointerException> { createTransactionStep.getNextStep(chatId) }

        assertAll(
            { verify(transactionDraftRepository).getTransactionDraft(chatId) },
            { verify(txService, never()).addTransaction(any()) }
        )
    }
}