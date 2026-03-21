package ru.telegram.bot.adapter.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import ru.telegram.bot.adapter.exceptions.MaxLengthExceededException

class StringValidationTest {

    private val text = "Hello"

    @Test
    fun `validateStringLength should not throw exception when string length is less than max length`() {
        assertDoesNotThrow { text.validateStringLength(maxLength = 10, errorMsg = "String too long") }
    }

    @Test
    fun `validateStringLength should not throw exception when string length equals max length`() {
        assertDoesNotThrow { text.validateStringLength(maxLength = 5, errorMsg = "String too long") }
    }

    @Test
    fun `validateStringLength should throw MaxLengthExceededException when string length exceeds max length`() {
        val text = "Hello World"
        val errorMsg = "String exceeds maximum length of 5"

        val exception = assertThrows<MaxLengthExceededException> { text.validateStringLength(maxLength = 5, errorMsg = errorMsg) }

        assertEquals(errorMsg, exception.message)
    }

    @Test
    fun `validateStringLength should handle empty string`() {
        val text = ""

        assertDoesNotThrow { text.validateStringLength(maxLength = 1, errorMsg = "String too long") }
    }

    @Test
    fun `validateStringLength should handle maxLength zero`() {
        val text = "a"
        val errorMsg = "String must be empty"

        val exception = assertThrows<MaxLengthExceededException> { text.validateStringLength(maxLength = 0, errorMsg = errorMsg) }

        assertEquals(errorMsg, exception.message)
    }
}