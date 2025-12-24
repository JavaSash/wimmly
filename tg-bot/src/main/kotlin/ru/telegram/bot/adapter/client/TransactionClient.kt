package ru.telegram.bot.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.telegram.bot.adapter.dto.budget.TransactionRq

@FeignClient(name = "budget-tx", url = "\${clients.budget.url}")
interface TransactionClient {

    @PostMapping("/api/transaction")
    fun addTransaction(@RequestBody request: TransactionRq): String
}