package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formSearchContext
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.TransactionService

@ExtendWith(MockitoExtension::class)
class RemoveTransactionStepTest {
    @Mock
    lateinit var searchContextRepository: SearchContextRepository

    @Mock
    lateinit var transactionService: TransactionService

    private lateinit var removeTransactionStep: RemoveTransactionStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        removeTransactionStep = RemoveTransactionStep(searchContextRepository, transactionService)
    }

    @Test
    fun `getNextStep should remove transaction and return FINAL`() {
        val searchContext = formSearchContext()
        whenever(searchContextRepository.findById(chatId)).thenReturn(searchContext)
        doNothing().whenever(transactionService).removeTransaction(any(), any())

        val result = removeTransactionStep.getNextStep(chatId)

        assertAll(
            { verify(searchContextRepository).findById(chatId) },
            { verify(transactionService).removeTransaction(chatId, searchContext.trxId!!) },
            { assertEquals(StepCode.FINAL, result) }
        )
    }

    @Test
    fun `getNextStep should throw exception when searchContext is null`() {
        whenever(searchContextRepository.findById(chatId)).thenReturn(null)

        assertThrows<NullPointerException> {
            removeTransactionStep.getNextStep(chatId)
        }

        assertAll(
            { verify(searchContextRepository).findById(chatId) },
            { verify(transactionService, never()).removeTransaction(any(), any()) }
        )
    }

    @Test
    fun `getNextStep should throw exception when trxId is null`() {
        val searchContext = formSearchContext().copy(trxId = null)
        whenever(searchContextRepository.findById(chatId)).thenReturn(searchContext)

        assertThrows<NullPointerException> {
            removeTransactionStep.getNextStep(chatId)
        }

        assertAll(
            { verify(searchContextRepository).findById(chatId) },
            { verify(transactionService, never()).removeTransaction(any(), any()) }
        )
    }
}