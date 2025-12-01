package ru.wimmly.logic

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.wimmly.logic.TestConstants.User.USER_ID
import ru.wimmly.logic.TestConstants.User.USER_NAME
import ru.wimmly.logic.model.entity.TransactionEntity
import ru.wimmly.logic.model.entity.UserEntity
import ru.wimmly.logic.model.transaction.TransactionCategory
import ru.wimmly.logic.model.transaction.TransactionType
import ru.wimmly.logic.repository.TransactionRepository
import ru.wimmly.logic.repository.UserRepository
import java.util.*

@SpringBootTest
class BasicTest : TestConfig() {
    @Autowired
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var txRepo: TransactionRepository

    @BeforeEach
    fun setup() {
        txRepo.deleteAll()
        userRepo.deleteAll()
    }

    protected fun initUser(
        userId: String = USER_ID,
        name: String = USER_NAME
    ): UserEntity = userRepo.save(
        UserEntity(
            tgId = userId,
            name = "Test User"
        )
    )

    protected fun initTransaction(
        userId: String = USER_ID,
        type: TransactionType = TransactionType.INCOME,
        amount: Double = 1500.00
    ): TransactionEntity = txRepo.save(
        TransactionEntity(
            id = UUID.randomUUID(),
            type = type,
            userId = userId,
            category = TransactionCategory.EDUCATION,
            amount = amount.money(),
            comment = "Salary part"
        )
    )
}