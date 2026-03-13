package ru.wimme.logic

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class TestConfig {
    companion object {

        val container = PostgreSQLContainer("postgres:16.2")
            .apply {
                withReuse(true)
                withUsername("test")
                withPassword("test")
                withDatabaseName("testdb")
                start()
            }

        @DynamicPropertySource
        @JvmStatic
        fun properties(registry: DynamicPropertyRegistry) {
            val c = container
            registry.add("spring.datasource.url") { c.jdbcUrl }
            registry.add("spring.datasource.username") { c.username }
            registry.add("spring.datasource.password") { c.password }
            registry.add("spring.liquibase.enabled") { true }
        }
    }
}