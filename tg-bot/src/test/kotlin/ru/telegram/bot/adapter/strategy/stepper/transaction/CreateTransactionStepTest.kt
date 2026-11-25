package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.generics.TelegramClient
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formTransactionDraft
import ru.telegram.bot.adapter.formTransactionRs
import ru.telegram.bot.adapter.repository.TransactionDraftRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.message.transaction.CreateTransactionMessage

@ExtendWith(MockitoExtension::class)
class CreateTransactionStepTest {
    @Mock
    lateinit var transactionDraftRepository: TransactionDraftRepository
    @Mock
    lateinit var txService: TransactionService
    @Mock
    lateinit var confirmation: CreateTransactionMessage
    @Mock
    lateinit var telegramClient: TelegramClient

    private lateinit var createTransactionStep: CreateTransactionStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        createTransactionStep = CreateTransactionStep(transactionDraftRepository, txService, confirmation, telegramClient)
    }

    @Test
    fun `getNextStep should confirm the saved transaction and return BALANCE`() {
        val trxDraft = formTransactionDraft()
        val transaction = formTransactionRs()
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(trxDraft)
        whenever(txService.addTransaction(trxDraft)).thenReturn(transaction)
        whenever(confirmation.render(transaction)).thenReturn("Транзакция успешно добавлена!")

        val result = createTransactionStep.getNextStep(chatId)

        val message = argumentCaptor<SendMessage>()
        inOrder(txService, confirmation, telegramClient) {
            verify(txService).addTransaction(trxDraft)
            verify(confirmation).render(transaction)
            verify(telegramClient).execute(message.capture())
        }
        assertAll(
            { assertEquals(chatId.toString(), message.firstValue.chatId) },
            { assertEquals("Транзакция успешно добавлена!", message.firstValue.text) },
            { assertEquals("HTML", message.firstValue.parseMode) },
            { assertInstanceOf(ReplyKeyboardRemove::class.java, message.firstValue.replyMarkup) },
            { assertEquals(StepCode.BALANCE, result) }
        )
    }

    @Test
    fun `getNextStep should skip confirmation when saving fails`() {
        val trxDraft = formTransactionDraft()
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(trxDraft)
        whenever(txService.addTransaction(trxDraft)).thenThrow(IllegalStateException("budget unavailable"))

        assertThrows<IllegalStateException> { createTransactionStep.getNextStep(chatId) }

        verifyNoInteractions(confirmation, telegramClient)
    }

    @Test
    fun `getNextStep should return BALANCE when confirmation delivery fails`() {
        val trxDraft = formTransactionDraft()
        val transaction = formTransactionRs()
        whenever(transactionDraftRepository.getTransactionDraft(chatId)).thenReturn(trxDraft)
        whenever(txService.addTransaction(trxDraft)).thenReturn(transaction)
        whenever(confirmation.render(transaction)).thenReturn("Транзакция успешно добавлена!")
        whenever(telegramClient.execute(any<SendMessage>())).thenThrow(IllegalStateException("Telegram unavailable"))

        assertEquals(StepCode.BALANCE, createTransactionStep.getNextStep(chatId))
        verify(txService).addTransaction(trxDraft)
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
