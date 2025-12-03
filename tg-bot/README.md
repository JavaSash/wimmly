# telegram-bot-kotlin-template
Sample code for telegram bot with Spring and Kotlin

Please, generate your telegram bot name before use. And change properties bot.username and bot.token in application.yml file

```yml
bot:
  username: // write your bot username
  token: // write your bot token
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/kotlin_template
    username: postgres
    password: postgres
```

Check bot token:
```
https://api.telegram.org/bot{token}/getMe
```

Before use this example bot try to create database and run command

```bash
/gradelw flywayMigrate
```

Don't forget change your properties in [build.gradle.kts](build.gradle.kts)

## Code packages guide
api — Классы, которые относятся к непосредственному взаимодействию с Телеграм API (отправка и получение данных)
command — Список команд телеграм бота
component — Прочие бины.
config — Конфигурация приложения
dto — DTO классы, енамы
event — Список классов для формирования ивентов Application Publisher
listener — Приём событий Application Publisher
repository — Слой взаимодействия с СУБД
service — Сервисы приложения
strategy — Стратегии. Это те компоненты, которые нужно менять, добавлять и удалять по ходу изменения бизнес процессов

[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)