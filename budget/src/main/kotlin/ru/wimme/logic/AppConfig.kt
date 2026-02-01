package ru.wimme.logic

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun jacksonModule(): KotlinModule = KotlinModule.Builder().build()
}