package ru.wimmly.logic.service

import org.springframework.stereotype.Service
import ru.wimmly.logic.model.report.Balance
import ru.wimmly.logic.model.transaction.TransactionType
import java.math.BigDecimal

@Service
class BalanceService(
    private val txService: TransactionService
) {

    fun getBalance(userId: String): Balance {
        val transactions = txService.getUserTransactions(userId)

        var income = BigDecimal.ZERO
        var expense = BigDecimal.ZERO

        transactions.forEach {
            when (it.type) {
                TransactionType.INCOME -> income = income.add(it.amount)
                TransactionType.EXPENSE -> expense = expense.add(it.amount)
            }
        }

        val balance = income.subtract(expense)

        return Balance(
            income = income,
            expense = expense,
            balance = balance
        )
    }
}