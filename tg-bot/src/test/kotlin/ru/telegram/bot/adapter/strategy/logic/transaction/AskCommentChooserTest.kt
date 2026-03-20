package ru.telegram.bot.adapter.strategy.logic.transaction

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import ru.telegram.bot.adapter.TestConstants.Chat.CHAT_ID
import ru.telegram.bot.adapter.dto.enums.ExecuteStatus
import ru.telegram.bot.adapter.formCallbackQuery
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.utils.Constants.Button.NO
import ru.telegram.bot.adapter.utils.Constants.Button.YES

@ExtendWith(MockitoExtension::class)
class AskCommentChooserTest {
    @Mock
    lateinit var chatContextRepository: ChatContextRepository

    private lateinit var chooser: AskCommentChooser

    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        chooser = AskCommentChooser(chatContextRepository)
    }

    @Test
    fun `execute should set accept true and return FINAL for YES`() {
        val callback = formCallbackQuery(data = YES)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(chatContextRepository).updateAccept(chatId, true) },
        )
    }

    @Test
    fun `execute should set accept false and clear comment for NO`() {
        val callback = formCallbackQuery(data = NO)

        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.FINAL, result) },
            { verify(chatContextRepository).updateAccept(chatId, false) },
        )
    }

    @Test
    fun `execute should do nothing for unknown callback`() {
        val callback = formCallbackQuery(data = "UNKNOWN")


        val result = chooser.execute(chatId, callback)

        assertAll(
            { assertEquals(ExecuteStatus.NOTHING, result) },
            { verify(chatContextRepository, never()).updateAccept(any(), any()) },
        )
    }
}