package ru.telegram.bot.adapter.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import ru.telegram.bot.adapter.dto.budget.user.UserCheckRq
import ru.telegram.bot.adapter.dto.budget.user.UserRegistrationRq
import ru.telegram.bot.adapter.dto.budget.user.UserRegistrationRs

@FeignClient(name = "budget-user", url = "\${clients.budget.url}")
interface UserClient {

    @PostMapping("/api/user/check")
    fun isUserExist(@RequestBody request: UserCheckRq): Boolean


    @PostMapping("/api/user/register")
    fun registerUser(@RequestBody request: UserRegistrationRq): UserRegistrationRs
}