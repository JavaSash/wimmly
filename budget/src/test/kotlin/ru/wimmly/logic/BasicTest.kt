package ru.wimmly.logic

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.wimmly.logic.model.entity.TransactionEntity
import ru.wimmly.logic.model.entity.UserEntity
import ru.wimmly.logic.model.transaction.TransactionCategory
import ru.wimmly.logic.model.transaction.TransactionType
import ru.wimmly.logic.repository.TransactionRepository
import ru.wimmly.logic.repository.UserRepository
import java.math.BigDecimal
import java.util.*

@SpringBootTest
class BasicTest: TestConfig() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    fun setup() {
        transactionRepository.deleteAll()
        userRepository.deleteAll()
    }

    protected fun initUser(): UserEntity = userRepository.save(
            UserEntity(
                tgId = "test_user_123",
                name = "Test User"
            )
        )

    protected fun initTransaction(): TransactionEntity = transactionRepository.save(
            TransactionEntity(
                id = UUID.randomUUID(),
                type = TransactionType.INCOME,
                userId = "test_user",
                category = TransactionCategory.EDUCATION,
                amount = BigDecimal("1500.00"),
                comment = "Salary part"
            )
        )
}