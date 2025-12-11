package ru.telegram.bot.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.telegram.bot.adapter.strategy.dto.BalanceDto

@FeignClient(name = "budget-report", url = "\${clients.budget.url}")
interface ReportClient {

    @GetMapping("/api/report/balance/{userId}")
    fun getBalance(@PathVariable userId: String): BalanceDto
}