package ru.wimme.logic.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.wimme.logic.model.user.UserCheckRq
import ru.wimme.logic.model.user.UserRegistrationRq
import ru.wimme.logic.model.user.UserRegistrationRs
import ru.wimme.logic.service.UserService

@RestController
@RequestMapping("/api/user")
class UserApi(
    private val userService: UserService
) {
    @PostMapping("/check")
    fun checkUser(@RequestBody request: UserCheckRq): Boolean = userService.isRegistered(request.telegramUserId)


    @PostMapping("/register")
    fun registerUser(@RequestBody request: UserRegistrationRq): UserRegistrationRs =
        userService.register(request)

}