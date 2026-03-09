package ru.telegram.bot.adapter.strategy.data.transaction

import mu.KLogging
import org.springframework.stereotype.Repository
import ru.telegram.bot.adapter.dto.budget.TransactionRs
import ru.telegram.bot.adapter.dto.budget.TransactionSearchRq
import ru.telegram.bot.adapter.dto.enums.StepCode
import ru.telegram.bot.adapter.repository.ChatContextRepository
import ru.telegram.bot.adapter.repository.SearchContextRepository
import ru.telegram.bot.adapter.service.TransactionService
import ru.telegram.bot.adapter.strategy.data.AbstractRepository
import ru.telegram.bot.adapter.strategy.dto.ShowTransactionsDto
import ru.telegram.bot.adapter.strategy.dto.TransactionItem
import ru.telegram.bot.adapter.utils.Constants.Transaction.DATE_FORMAT
import ru.telegram.bot.adapter.utils.formatDate
import ru.telegram.bot.adapter.utils.formatMoney

/**
 * Data provider for transactions view
 * Should extend from AbstractRepository to work with MessageContext<T : DataModel>
 * T should extend from DataModel
 */
@Repository
class ShowTransactionsRepository(
    private val transactionService: TransactionService,
    private val searchContextRepository: SearchContextRepository,
    private val chatContextRepository: ChatContextRepository,
) : AbstractRepository<ShowTransactionsDto>() {
    companion object : KLogging()

    override fun getData(chatId: Long): ShowTransactionsDto {
        logger.info { "$$$ ShowTransactionsRepository.getData for chatId=$chatId" }
        val searchCtx = searchContextRepository.findById(chatId)!!
        val flowCtx = chatContextRepository.getUser(chatId)!!.flowContext
        logger.debug { "$$$ [ShowTransactionsRepository] found search context: $searchCtx for flow: $flowCtx" }

        val transactions: List<TransactionRs> = when (flowCtx) {
            StepCode.SEARCH_TRANSACTIONS.name -> {
                transactionService.getTransactionsWithFilters(
                    rq = TransactionSearchRq(
                        type = searchCtx.type!!,
                        userId = searchCtx.chatId.toString(),
                        category = searchCtx.category!!,
                        limit = 10, // выдаём в МВП 10 последних транзакций
                        displayId = null
                    )
                )
            }

            StepCode.DELETE_TRANSACTION.name -> {
                transactionService.getUserTransactionByDisplayId(chatId, searchCtx.trxId!!)
                    ?.let { listOf(it) }
                    ?: emptyList()
            }

            else -> {
                logger.error { "$$$ Not supported flow: $flowCtx for ShowTransactions Step" }
                emptyList()
            }
        }
        logger.debug { "$$$ [ShowTransactionsRepository] found transactions from budget: $transactions" }

        return ShowTransactionsDto(
            transactions = transactions.map { tx ->
                TransactionItem(
                    displayId = tx.displayId,
                    formattedDate = tx.createdAt.formatDate(DATE_FORMAT),
                    category = tx.category,
                    type = tx.type,
                    amount = tx.amount.formatMoney(),
                    comment = tx.comment
                )
            }
        ).also { logger.debug { "$$$ [ShowTransactionsRepository] form ShowTransactionsDto: $it" } }
    }

}