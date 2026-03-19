package ru.telegram.bot.adapter.strategy.data.report

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_100
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_150
import ru.telegram.bot.adapter.TestConstants.Tx.AMOUNT_50
import ru.telegram.bot.adapter.TestConstants.User.CHAT_ID
import ru.telegram.bot.adapter.client.ReportClient
import ru.telegram.bot.adapter.strategy.dto.BalanceDto
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class BalanceRepositoryTest {

    @Mock
    lateinit var reportClient: ReportClient

    private lateinit var repository: BalanceRepository
    private val chatId = CHAT_ID

    @BeforeEach
    fun setup() {
        repository = BalanceRepository(reportClient)
    }

    @Test
    fun `getData should return balance from client`() {
        val report = BalanceDto(
            balance = AMOUNT_150,
            income = AMOUNT_100,
            expense = AMOUNT_50
        )

        whenever(reportClient.getBalance(chatId.toString())).thenReturn(report)

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(report.balance, result.balance) },
            { assertEquals(report.income, result.income) },
            { assertEquals(report.expense, result.expense) }
        )
    }

    @Test
    fun `getData should return stub when client throws exception`() {
        whenever(reportClient.getBalance(chatId.toString())).thenThrow(RuntimeException("boom"))

        val result = repository.getData(chatId)

        assertAll(
            { assertEquals(BigDecimal.ZERO, result.balance) },
            { assertEquals(BigDecimal.ZERO, result.income) },
            { assertEquals(BigDecimal.ZERO, result.expense) }
        )
    }
}