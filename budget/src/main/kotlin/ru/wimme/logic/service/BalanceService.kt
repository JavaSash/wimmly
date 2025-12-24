package ru.wimme.logic.service

import org.springframework.stereotype.Service
import ru.wimme.logic.model.report.Balance
import ru.wimme.logic.model.report.Period
import ru.wimme.logic.model.transaction.TransactionType
import java.math.BigDecimal

@Service
class BalanceService(
    private val txService: TransactionService
) {

    fun getBalance(userId: String, period: Period? = null): Balance {
        val transactions = if (period != null) {
            txService.getUserTransactions(userId)
                .filter { it.createdAt!!.isAfter(period.from) && it.createdAt.isBefore(period.to) }
        } else {
            txService.getUserTransactions(userId)
        }

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