package ru.wimme.logic

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import ru.wimme.logic.TestConstants.User.USER_ID
import ru.wimme.logic.TestConstants.User.USER_NAME
import ru.wimme.logic.model.entity.TransactionEntity
import ru.wimme.logic.model.entity.UserEntity
import ru.wimme.logic.model.transaction.ExpenseCategory
import ru.wimme.logic.model.transaction.TransactionType
import ru.wimme.logic.repository.TransactionRepository
import ru.wimme.logic.repository.UserRepository
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
            firstName = "Ali Baba",
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
            category = ExpenseCategory.EDUCATION.name,
            amount = amount.money(),
            comment = "Salary part"
        )
    )

    protected fun initTransactionWithCreatedAt(
        userId: String,
        type: TransactionType,
        amount: Double,
        createdAt: LocalDateTime,
        category: ExpenseCategory = ExpenseCategory.EDUCATION
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