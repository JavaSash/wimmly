package ru.wimme.logic.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import ru.wimme.logic.BasicTest
import ru.wimme.logic.TestConstants.User.USER_ID
import ru.wimme.logic.TestConstants.User.USER_NAME
import ru.wimme.logic.exception.NotFoundException
import ru.wimme.logic.model.user.UserRegistrationRq

class UserServiceTest : BasicTest() {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun `isRegistered returns false for unknown user`() {
        assertFalse(userService.isRegistered(USER_ID))
    }

    @Test
    fun `isRegistered returns true for existing user`() {
        val user = initUser(USER_ID)
        assertTrue(userService.isRegistered(user.tgId))
    }

    @Test
    fun `register creates new user when not registered`() {
        val rq = UserRegistrationRq(telegramUserId = USER_ID, name = USER_NAME)
        val rs = userService.register(rq)


        val savedUser = userService.getUser(USER_ID)
        assertAll(
            { assertEquals(USER_ID, rs.userId) },
            { assertEquals(rq.name, savedUser.name) },
        )
    }

    @Test
    fun `register returns existing user when already registered`() {
        val existingUser = initUser(USER_ID)
        val rq = UserRegistrationRq(telegramUserId = USER_ID, name = USER_NAME)

        val rs = userService.register(rq)
        assertEquals(existingUser.tgId, rs.userId)
    }

    @Disabled // todo add jakarta validation
    @Test
    fun `register with empty name throws exception`() {
        val rq = UserRegistrationRq(telegramUserId = USER_ID, name = "")
//        assertThrows<javax.validation.ConstraintViolationException> {
//            userService.register(rq)
//        }
    }

    @Test
    fun `getUser returns user when exists`() {
        val user = initUser(USER_ID)
        val foundUser = userService.getUser(USER_ID)

        assertAll(
            { assertEquals(user.tgId, foundUser.tgId) },
            { assertEquals(user.name, foundUser.name) },
        )
    }

    @Test
    fun `getUser throws NotFoundException when user does not exist`() {
        assertThrows<NotFoundException> {
            userService.getUser(USER_ID)
        }
    }

    @Disabled // todo add jakarta validation
    @Test
    fun `register with borderline tgId length`() {
        val maxLengthId = "a".repeat(255)
        val rq = UserRegistrationRq(telegramUserId = maxLengthId, name = "Max User")
        val rs = userService.register(rq)
        assertEquals(maxLengthId, rs.userId)
    }
}