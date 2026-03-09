package ru.telegram.bot.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import ru.telegram.bot.adapter.dto.budget.TransactionRq
import ru.telegram.bot.adapter.dto.budget.TransactionRs
import ru.telegram.bot.adapter.dto.budget.TransactionSearchRq

@FeignClient(name = "budget-tx", url = "\${clients.budget.url}")
interface TransactionClient {

    @PostMapping("/api/transaction")
    fun addTransaction(@RequestBody request: TransactionRq): String

    @PostMapping("/api/transaction/search")
    fun getTransactionsWithFilters(@RequestBody rq: TransactionSearchRq): List<TransactionRs>

    @GetMapping("/api/transaction/exist/user/{userId}/{displayId}")
    fun isExist(@PathVariable userId: Long, @PathVariable displayId: Long): Boolean

    @DeleteMapping("/api/transaction/user/{userId}/{displayId}")
    fun deleteByDisplayId(@PathVariable userId: Long, @PathVariable displayId: Long)
}