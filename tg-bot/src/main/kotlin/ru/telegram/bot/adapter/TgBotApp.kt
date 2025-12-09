package ru.telegram.bot.adapter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class TgBotApp

fun main(args: Array<String>) {
    runApplication<TgBotApp>(*args)
}