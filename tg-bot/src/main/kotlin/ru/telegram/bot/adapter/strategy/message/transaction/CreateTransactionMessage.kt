package ru.telegram.bot.adapter.strategy.message.transaction

import org.springframework.stereotype.Component
import ru.telegram.bot.adapter.dto.budget.TransactionRs
import ru.telegram.bot.adapter.service.MessageWriter
import ru.telegram.bot.adapter.strategy.data.transaction.SelectCategoryRepository
import ru.telegram.bot.adapter.strategy.dto.TransactionItem
import ru.telegram.bot.adapter.strategy.message.common.AbstractSendMessage
import ru.telegram.bot.adapter.utils.Constants.Transaction.DATE_FORMAT
import ru.telegram.bot.adapter.utils.Constants.Transaction.EXPENSE_RU
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME
import ru.telegram.bot.adapter.utils.Constants.Transaction.INCOME_RU
import ru.telegram.bot.adapter.utils.formatDate
import ru.telegram.bot.adapter.utils.formatMoney

@Component
class CreateTransactionMessage(
    messageWriter: MessageWriter,
    private val categoryRepository: SelectCategoryRepository,
) : AbstractSendMessage<TransactionItem>(messageWriter) {

    fun render(transaction: TransactionRs): String {
        val category = categoryRepository.getCategories(transaction.type)
            .firstOrNull { it.code == transaction.category }
        return message(
            TransactionItem(
                displayId = transaction.displayId,
                // budget stores the transaction date in createdAt.
                formattedDate = transaction.createdAt.formatDate(DATE_FORMAT),
                category = category?.description ?: transaction.category,
                type = if (transaction.type == INCOME) INCOME_RU else EXPENSE_RU,
                amount = transaction.amount.formatMoney(),
                comment = transaction.comment?.takeIf { it.isNotBlank() },
            )
        )
    }
}
