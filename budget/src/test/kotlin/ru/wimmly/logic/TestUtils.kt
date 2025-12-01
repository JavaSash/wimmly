package ru.wimmly.logic

import org.testcontainers.containers.PostgreSQLContainer

object PostgresSingleton {
    val container = PostgreSQLContainer("postgres:16.2")
        .apply {
            withReuse(true)
            withUsername("test")
            withPassword("test")
            withDatabaseName("testdb")
            start()
        }
}