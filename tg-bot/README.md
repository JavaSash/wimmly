# telegram-bot-kotlin
Adapter for Telegram API

1. Receive messages from Tg
2. Convert them into internal events/commands
3. Send to backend (REST API, events)
4. Receive responses from back and send it to Tg

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

### Create new command
1. Create new class NewCommand extended from AbstractCommand
2. Add value for new command in BotCommand
3. Pass new enum value to NewCommand class as first param
4. Create class for new message in package message
5. Create .ftl file with response txt
6. Create new class extended from MessageChooser
Name should be with prefix StepCode.value and postfix Chooser
For example: StepCode.BALANCE and BalanceChooser
7. Add new class extended from Step and implement method getNextStep()
8. Add repository class extended from AbstractRepository<T> (type is DTO class) to provide data if needed

### Create step
1. Create new value in StepCode enum
2. Create step class and override getNextStep
3. Create dto extended from DataModel (if custom DTO needed)
4. Create message class extended from AbstractSendMessage<T> with type from 3.
5. Create .ftl file with response txt
6. Create chooser class extended from MessageChooser

### Create step with button
1. Create repository extended from AbstractRepository<T> (SelectCategoryRepository, type SelectCategoryDto)
2. Create DTO extended from DataModel for repository (SelectCategoryDto)
3. Create message class extended from AbstractSendMessage<T> (SelectCategoryMessage, type SelectCategoryDto)
4. Override in message class methods: message(), inlineButtons(), replyButtons()
5. Create .ftl template file with same name (as message class without postfix Message)
6. Create chooser class extended from CallbackChooser (SelectCategoryChooser)
7. Create step class and override getNextStep

## TODO
1. сделать все нужные операции (под апи бэка)
2. Упростить/удалить стратегии? - они переедут в бэкенд 
3. Модифицировать слушателей для отправки запросов в бэкенд 
4. Оставить только преобразование Telegram → внутренний формат
5. сделать кнопки категорий в UI на русском, хранение в бд на англ
6. Добавить транзакцию за дату (доход/расход)
7. Детализация доходов за период (день, неделя, мес)
8. Детализация расходов за период (день, неделя, мес)
9. Детализация по категории за период (день, неделя, мес)
10. Редактировать транзакцию


### UI


### Links
[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)