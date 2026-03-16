package ru.telegram.bot.adapter.service

import freemarker.template.Configuration
import freemarker.template.Template
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import ru.telegram.bot.adapter.dto.enums.StepCode
import java.io.StringWriter

@ExtendWith(MockitoExtension::class)
class MessageWriterTest {

    @Mock
    lateinit var freeMarkerConfigurer: FreeMarkerConfigurer

    @Mock
    lateinit var configuration: Configuration

    @Mock
    lateinit var template: Template

    @InjectMocks
    lateinit var messageWriter: MessageWriter

    @Test
    fun `process should render template with data`() {
        val step = StepCode.START
        val model = mapOf("name" to "John")

        whenever(freeMarkerConfigurer.configuration).thenReturn(configuration)
        whenever(configuration.getTemplate("start.ftl")).thenReturn(template)

        doAnswer {
            val writer = it.getArgument<StringWriter>(1)
            writer.write("rendered text")
            null
        }.whenever(template).process(any(), any())

        val result = messageWriter.process(step, model)

        val dataCaptor = argumentCaptor<Map<String, Any>>()
        val writerCaptor = argumentCaptor<StringWriter>()

        verify(template).process(dataCaptor.capture(), writerCaptor.capture())

        assertAll(
            { assertEquals("rendered text", result) },
            { assertTrue(dataCaptor.firstValue.containsKey("data")) },
            { assertEquals(model, dataCaptor.firstValue["data"]) }
        )
    }

    @Test
    fun `process should render template without data`() {
        val step = StepCode.START

        whenever(freeMarkerConfigurer.configuration).thenReturn(configuration)
        whenever(configuration.getTemplate("start.ftl")).thenReturn(template)

        doAnswer {
            val writer = it.getArgument<StringWriter>(1)
            writer.write("no data text")
            null
        }.whenever(template).process(any(), any())

        val result = messageWriter.process(step)

        val dataCaptor = argumentCaptor<Map<String, Any>>()

        verify(template).process(dataCaptor.capture(), any())

        assertAll(
            { assertEquals("no data text", result) },
            { assertTrue(dataCaptor.firstValue.isEmpty()) }
        )
    }

    @Test
    fun `process should request template by lowercase step name`() {
        val step = StepCode.START

        whenever(freeMarkerConfigurer.configuration).thenReturn(configuration)
        whenever(configuration.getTemplate(any())).thenReturn(template)

        doNothing().whenever(template).process(any(), any())

        messageWriter.process(step)

        verify(configuration).getTemplate("start.ftl")
    }
}