# telegram-bot-kotlin

## Preparation
### Create Telegram bot
1. Generate your telegram bot before use. 
2. Check bot token:
```
https://api.telegram.org/bot{token}/getMe
```
3. Change properties bot.username and bot.token in [application.yml](src%2Fmain%2Fresources%2Fapplication.yml)
```yml
bot:
  username: // write your bot username
  token: // write your bot token
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bot
    username: postgres
    password: postgres
```

### Prepare database and POJO
1. Run Postgres DB with name "bot" ([docker-compose.yml](src%2Fmain%2Fresources%2Fdocker-compose.yml))
2. Add JDBC URL and credentials in [application.yml](src%2Fmain%2Fresources%2Fapplication.yml) 
3. Add JDBC URL and credentials in [build.gradle.kts](build.gradle.kts) in case they don't catch up from the app.yml
4. Run migrations
```bash
/gradelw flywayMigrate
```
5. Generate POJO classes for DSL with jooq
```
./gradlew generateJooq
```

## Info
### Code packages guide
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

### Data stream
```
Пользователь → Telegram → Бот → [Событие] → [Стратегия] → [Бизнес-логика] → Ответ
```
1. Пользователь отправляет сообщение/команду 
2. [ApplicationListener.kt](src%2Fmain%2Fkotlin%2Fru%2Ftemplate%2Ftelegram%2Fbot%2Fkotlin%2Ftemplate%2Flistener%2FApplicationListener.kt) получает обновление 
3. Создается событие: 
* [TgReceivedCallbackEvent.kt](src%2Fmain%2Fkotlin%2Fru%2Ftemplate%2Ftelegram%2Fbot%2Fkotlin%2Ftemplate%2Fevent%2FTgReceivedCallbackEvent.kt)
* [TgReceivedMessageEvent.kt](src%2Fmain%2Fkotlin%2Fru%2Ftemplate%2Ftelegram%2Fbot%2Fkotlin%2Ftemplate%2Fevent%2FTgReceivedMessageEvent.kt)
* [TgStepMessageEvent.kt](src%2Fmain%2Fkotlin%2Fru%2Ftemplate%2Ftelegram%2Fbot%2Fkotlin%2Ftemplate%2Fevent%2FTgStepMessageEvent.kt)
4. ApplicationEventPublisher (Spring) публикует событие 
5. Соответствующий Listener (из п.2) ловит событие 
6. Определяется текущая стратегия пользователя (по step_code из БД)
7. Стратегия выполняет бизнес-логику 
8. Отправляется ответ через [TelegramConsumer.kt](src%2Fmain%2Fkotlin%2Fru%2Ftemplate%2Ftelegram%2Fbot%2Fkotlin%2Ftemplate%2Fapi%2FTelegramConsumer.kt)

TODO start from here
### Links
[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)