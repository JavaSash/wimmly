package ru.telegram.bot.adapter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TgBotApp

fun main(args: Array<String>) {
    runApplication<TgBotApp>(*args)
}