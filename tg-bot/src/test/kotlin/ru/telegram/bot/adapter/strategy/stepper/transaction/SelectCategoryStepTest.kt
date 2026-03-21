package ru.telegram.bot.adapter.strategy.stepper.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.formChatContext
import ru.telegram.bot.adapter.repository.ChatContextRepository

@ExtendWith(MockitoExtension::class)
class SelectCategoryStepTest {
    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    private lateinit var selectCategoryStep: SelectCategoryStep

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        selectCategoryStep = SelectCategoryStep(chatContextRepository)
    }

    @Test
    fun `getNextStep should return ENTER_AMOUNT when flowContext is ADD_INCOME`() {
        val user = formChatContext(flowContext = StepCode.ADD_INCOME.name)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = selectCategoryStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.ENTER_AMOUNT, result) }
        )
    }

    @Test
    fun `getNextStep should return ENTER_AMOUNT when flowContext is ADD_EXPENSE`() {
        val user = formChatContext(flowContext = StepCode.ADD_EXPENSE.name)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = selectCategoryStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.ENTER_AMOUNT, result) }
        )
    }

    @Test
    fun `getNextStep should return SHOW_TRANSACTIONS when flowContext is SEARCH_TRANSACTIONS`() {
        val user = formChatContext(flowContext = StepCode.SEARCH_TRANSACTIONS.name)
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = selectCategoryStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertEquals(StepCode.SHOW_TRANSACTIONS, result) }
        )
    }

    @Test
    fun `getNextStep should return null when flowContext is unknown`() {
        val user = formChatContext(flowContext = "UNKNOWN_FLOW")
        whenever(chatContextRepository.getUser(chatId)).thenReturn(user)

        val result = selectCategoryStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertNull(result) }
        )
    }

    @Test
    fun `getNextStep should return null when user is null`() {
        whenever(chatContextRepository.getUser(chatId)).thenReturn(null)

        val result = selectCategoryStep.getNextStep(chatId)

        assertAll(
            { verify(chatContextRepository).getUser(chatId) },
            { assertNull(result) }
        )
    }
}