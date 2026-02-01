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

### Create step without button
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
### MVP
1. сделать все нужные операции (под апи бэка)
   1) Детализация по категории за период (день, неделя, мес)
   2) Редактировать транзакцию
   3) Удалить транзакцию
   4) просмотр транзакций\поиск
   5) форма обратной связи
   6) /commands - список команд из /help вынести
   7) /help - guide, дисклеймер по ПД, не хранит данные об оплатах, только обезличенную инфу по ведению бюджета ()
2. impl stepCode: FINAL. Ошибка в логах:
   Caused by: org.telegram.telegrambots.meta.exceptions.TelegramApiException: Unable to execute sendmessage method
   Caused by: org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException: Text parameter can't be empty in method: SendMessage(chatId=****, messageThreadId=null, text=, parseMode=html, disableWebPagePreview=null, disableNotification=null, replyToMessageId=null, replyMarkup=ReplyKeyboardRemove(removeKeyboard=true, selective=null), entities=null, allowSendingWithoutReply=null, protectContent=null, linkPreviewOptions=null, replyParameters=null, businessConnectionId=null, messageEffectId=null, allowPaidBroadcast=null)
3. fix bug при запросе баланса за период  $$$ 0 found for period 2025-12-31T21:00:00Z - 2026-01-31T21:00:00Z
4. логи с путём пользователя? 
5. альфа-тестирование (внутреннее)
   1) составить набор функций, пользовательских сценариев, потенциально проблемные места
   2) проверка через UI (бот) - мобилка, ПК-версия
   3) тестирование API бэка
   4) фиксы по итогам альфы
   5) E2E-тесты (авто тесты\скрипты на питоне с имитацией пользовательских действий)
   6) фейл тесты (кривые команды, ошибки ввода, невалидные значения)
   7) тесты изоляции данных (пользователь видит только свои транзакции)
6. бэта-тестирование
   1) гайд как начать, канал для обратной связи
   2) выдача доступа знакомым, сбор фидбэка
   3) фиксы по итогам бэты (Что непонятно? Что неудобно? Каких функций не хватает? Сколько пользователей "зависло" после команды /start? Сколько добавило хотя бы одну запись?)
   4) дополнение бэклога
   5) настроить бэкапы бд
7. Public Beta
   1) продвижение в каталоге ботов, тематических каналах
   2) разбор обратной связи
   3) дополнение бэклога
8. сбор обратной связи -> планирование -> разработка -> тестирование -> релиз

### Backlog
1. Упростить/удалить стратегии? - они переедут в бэкенд 
2. Модифицировать слушателей для отправки запросов в бэкенд 
3. Оставить только преобразование Telegram → внутренний формат
4. сделать кнопки категорий в UI на русском, хранение в бд на англ 
5. Изучить на этапе выбора клауд платформы для разворачивания: https://sourcecraft.dev/portal/grant/?utm_source=habr&utm_medium=referral&utm_campaign=mini_banner3_201125/
6.
7. деплой в облако (конец января 2026?)
8. интеграция с календарём
9. интеграция с ОФД (чеки)
10. ИИ обработка чека
11. подписки
12. 

### UI

### QA
Test cases
1. Первый старт (регистрация пользователя в БД бота, бэка, показ приветственного сообщения и help сообщения)
2. Старт существующего пользователя 
3. Add income
   3.1 базовый (с датой по умолчанию (сегодняшней) и без коммента)
   3.2 с указанной датой
   3.3 с комментом
   3.4 с суммой без копеек
   3.5 с суммой с копейками
   3.6 с большой суммой 9999999999999999999999999.99
   3.7 с маленькой суммой 0.01
   3.8 с 0 суммой
4. Add expense
   4.1 базовый (с датой по умолчанию (сегодняшней) и без коммента)
   4.2 с указанной датой
   4.3 с комментом
   4.4 с суммой без копеек
   4.5 с суммой с копейками
   4.6 с большой суммой 9999999999999999999999999.99
   4.7 с маленькой суммой 0.01
   4.8 с 0 суммой
5. 

### Links
[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)
