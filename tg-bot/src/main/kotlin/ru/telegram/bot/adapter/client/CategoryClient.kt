package ru.telegram.bot.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import ru.telegram.bot.adapter.dto.business.CategoryDto

@FeignClient(name = "budget", url = "\${clients.budget.url}")
interface CategoryClient {

    @GetMapping("/api/v1/category/{type}")
    fun getCategories(@PathVariable("type") type: String): List<CategoryDto>
}