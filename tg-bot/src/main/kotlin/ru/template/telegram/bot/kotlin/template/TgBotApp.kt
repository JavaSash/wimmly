package ru.template.telegram.bot.kotlin.template

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TgBotApp

fun main(args: Array<String>) {
    runApplication<TgBotApp>(*args)
}