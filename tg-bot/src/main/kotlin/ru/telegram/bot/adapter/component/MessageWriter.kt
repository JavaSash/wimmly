package ru.telegram.bot.adapter.component

import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import ru.telegram.bot.adapter.dto.enums.StepCode
import java.io.StringWriter

/**
 * Map response message class to .ftl file
 */
@Component
class MessageWriter(
    private val freeMarkerConfigurer: FreeMarkerConfigurer
) {

    companion object : KLogging()

    fun process(stepCode: StepCode, freemarkerData: Any? = null): String {
        logger.info { "$$$ MessageWriter.process: \nstepCode: $stepCode\nfreemarkerData: $freemarkerData" }
        val name = stepCode.name.lowercase()
        return processed(freemarkerData?.let { mapOf("data" to it) } ?: emptyMap(), "$name.ftl")
    }

    private fun processed(data: Map<String, Any>, templateName: String): String {
        val template = freeMarkerConfigurer.configuration.getTemplate(templateName)
        val output = StringWriter()
        template.process(data, output)
        return output.toString()
    }

}
