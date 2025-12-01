package ru.wimmly.logic

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import ru.wimmly.logic.TestConstants.User.USER_ID
import ru.wimmly.logic.TestConstants.User.USER_NAME
import ru.wimmly.logic.model.entity.TransactionEntity
import ru.wimmly.logic.model.entity.UserEntity
import ru.wimmly.logic.model.transaction.TransactionCategory
import ru.wimmly.logic.model.transaction.TransactionType
import ru.wimmly.logic.repository.TransactionRepository
import ru.wimmly.logic.repository.UserRepository
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BasicTest : TestConfig() {
    @Autowired
    lateinit var userRepo: UserRepository

    @Autowired
    lateinit var txRepo: TransactionRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

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

    protected fun initTransactionWithCreatedAt(
        userId: String,
        type: TransactionType,
        amount: Double,
        createdAt: LocalDateTime,
        category: TransactionCategory = TransactionCategory.EDUCATION
    ) {
        val id = UUID.randomUUID()
        jdbcTemplate.update(
            """
            INSERT INTO transactions
                (id, type, user_id, category, amount, comment, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
                .trimIndent(),
            id,
            type.name,
            userId,
            category.name,
            amount,
            "Old tx",
            createdAt,
            createdAt
        )
    }
}