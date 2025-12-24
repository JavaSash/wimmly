package ru.telegram.bot.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.telegram.bot.adapter.dto.budget.CategoryDto

@FeignClient(name = "budget-category", url = "\${clients.budget.url}")
interface CategoryClient {

    @GetMapping("/api/category/{tx-type}")
    fun getCategories(@PathVariable("tx-type") type: String): List<CategoryDto>
}