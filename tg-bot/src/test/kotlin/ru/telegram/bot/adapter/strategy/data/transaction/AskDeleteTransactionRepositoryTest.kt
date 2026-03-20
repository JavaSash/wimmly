package ru.telegram.bot.adapter.strategy.data.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.utils.CommonUtils.currentStepCode
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

/**
 * Test for [ru.telegram.bot.adapter.strategy.data.common.AskYesNoRepository], [ru.telegram.bot.adapter.strategy.data.common.AbstractRepository]
 */
class AskDeleteTransactionRepositoryTest {

    private val repository = AskDeleteTransactionRepository()

    @Test
    fun `getData should return yes no options`() {
        val result = repository.getData(CHAT_ID)

        assertAll(
            { assertEquals(2, result.accept.size) },
            { assertEquals(listOf(YES, NO), result.accept) }
        )
    }

    @Test
    fun `isAvailableForCurrentStep should match repository step`() {
        val step = repository.currentStepCode("Repository")

        assertAll(
            {assertTrue(repository.isAvailableForCurrentStep(step))},
            { assertEquals(StepCode.ASK_DELETE_TRANSACTION, step) },
        )
    }

}