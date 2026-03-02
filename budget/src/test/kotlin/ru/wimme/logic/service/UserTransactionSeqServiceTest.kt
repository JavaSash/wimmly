package ru.wimme.logic.service

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import ru.wimme.logic.BasicTest
import ru.wimme.logic.TestConstants.User.USER_ID_2
import ru.wimme.logic.model.entity.UserDisplayIdSeqEntity

class UserTransactionSeqServiceTest : BasicTest() {

    @Autowired
    private lateinit var userTransactionSeqService: UserTransactionSeqService

    @Test
    fun `getNextSeq should return 1 for new user`() {
        val userId = initUser().tgId

        val result = userTransactionSeqService.getNextSeq(userId)

        assertAll(
            { assertEquals(1L, result) },
            { assertEquals(1L, userDisplayIdSeqRepository.findByIdOrNull(userId)?.currentSeq) }
        )
    }

    @Test
    fun `getNextSeq should increment existing sequence`() {
        val userId = initUser().tgId
        userDisplayIdSeqRepository.save(UserDisplayIdSeqEntity(userId = userId, currentSeq = 5))

        val result = userTransactionSeqService.getNextSeq(userId)

        assertAll(
            { assertEquals(6L, result) },
            {
                val seq = userDisplayIdSeqRepository.findByIdOrNull(userId)
                assertEquals(6L, seq?.currentSeq)
            }
        )
    }

    @Test
    fun `getNextSeq should handle multiple increments`() {
        val userId = initUser().tgId

        val result1 = userTransactionSeqService.getNextSeq(userId)
        val result2 = userTransactionSeqService.getNextSeq(userId)
        val result3 = userTransactionSeqService.getNextSeq(userId)

        assertAll(
            { assertEquals(1L, result1) },
            { assertEquals(2L, result2) },
            { assertEquals(3L, result3) },
            { assertEquals(3L, userDisplayIdSeqRepository.findByIdOrNull(userId)?.currentSeq) }
        )
    }

    @Test
    fun `getNextSeq should work for different users independently`() {
        val user1 = initUser().tgId
        val user2 = initUser(userId = USER_ID_2).tgId

        val result1User1 = userTransactionSeqService.getNextSeq(user1)
        val result1User2 = userTransactionSeqService.getNextSeq(user2)
        val result2User1 = userTransactionSeqService.getNextSeq(user1)

        assertAll(
            { assertEquals(1L, result1User1) },
            { assertEquals(1L, result1User2) },
            { assertEquals(2L, result2User1) },
            { assertEquals(2L, userDisplayIdSeqRepository.findByIdOrNull(user1)?.currentSeq) },
            { assertEquals(1L, userDisplayIdSeqRepository.findByIdOrNull(user2)?.currentSeq) }
        )
    }

    @Test
    fun `getNextSeq should work with existing transactions`() {
        val user = initUser()
        val userId = user.tgId

        // Создаем несколько транзакций с display_id
        initTransaction(userId = userId, displayId = 1L)
        initTransaction(userId = userId, displayId = 2L)
        initTransaction(userId = userId, displayId = 3L)

        // Создаем счетчик со значением 3 (максимальный display_id)
        userDisplayIdSeqRepository.save(UserDisplayIdSeqEntity(userId = userId, currentSeq = 3))

        val result = userTransactionSeqService.getNextSeq(userId)

        assertAll(
            { assertEquals(4L, result) },
            { assertEquals(4L, userDisplayIdSeqRepository.findByIdOrNull(userId)?.currentSeq) }
        )
    }
}