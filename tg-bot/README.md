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
6. Create new class extended from MessageChooser (only for steps, which needed user's response)
Name should be with prefix StepCode.value and postfix Chooser
For example: StepCode.BALANCE and BalanceChooser
7. Add new class extended from Step and implement method getNextStep()
8. Add repository class extended from AbstractRepository<T> (type is DTO class) to provide data if needed
9. Add new command to /help ftl and to bot commands in tg

### Create step without button
1. Create new value in StepCode enum
2. Create step class and override getNextStep
3. Create dto extended from DataModel (if custom DTO needed)
4. Create message class extended from AbstractSendMessage<T> with type from 3.
5. Create .ftl file with response txt
6. Create chooser class extended from MessageChooser (only for steps, which needed user's response)

### Create step with button
1. Create repository extended from AbstractRepository<T> (SelectCategoryRepository, type SelectCategoryDto) (if data needed to form buttons)
2. Create DTO extended from DataModel for repository (SelectCategoryDto)
3. Create message class extended from AbstractSendMessage<T> (SelectCategoryMessage, type SelectCategoryDto)
4. Override in message class methods: message(), inlineButtons(), replyButtons()
5. Create .ftl template file with same name (as message class without postfix Message)
6. Create chooser class extended from CallbackChooser (SelectCategoryChooser)
7. Create step class and override getNextStep

### Add validation error on step with user input
1. Add new exception extended from [ValidationException.kt](src%2Fmain%2Fkotlin%2Fru%2Ftelegram%2Fbot%2Fadapter%2Fexceptions%2FValidationException.kt)
2. Add in execute() of Chooser class runCatching block with validation check (it should throw ValidationException) and in onFailure call logError() from [ErrorService.kt](src%2Fmain%2Fkotlin%2Fru%2Ftelegram%2Fbot%2Fadapter%2Fservice%2FErrorService.kt)
3. Add in getNextStep() call resolveNextStep() of ErrorService class with onSuccessStep (next step on success flow)

### Users flow
#### Общее
1. Запуск бота
   * Первый раз
   * пользователь уже зарегистрирован

#### Работа с транзакциями
1. Добавление транзакции 
   -> выбор типа транзакции
   -> выбор категории
   -> ввод суммы
   -> ввод даты (опционально)
   -> ввод комментария (опционально)

2. Поиск транзакции
   -> выбор типа транзакции
   -> выбор категории
   -> показ 10 последних транзакций

3. Удаление транзакции
   -> /delete_transaction
   -> Введите номер транзакции
   -> ввод pretty id транзакции
   -> вывод информации о транзакции
   -> удалить транзакцию? 
   -> кнопки Yes\No
4. 

#### Отчёты
Баланс
Отчёт за сегодня
Отчёт за неделю
Отчёт за месяц
Отчёт за год
Отчёт за всё время (TODO)

### QA
Test cases
1. Первый старт (регистрация пользователя в БД бота, бэка, показ приветственного сообщения и help сообщения)
2. Старт существующего пользователя (есть в БД бота, бэка)
3. Add income
   3.1 базовый (с датой по умолчанию (сегодняшней) и без коммента)
   3.2 с указанной датой
   3.3 с комментом
   3.4 с суммой без копеек
   3.5 с суммой с копейками
   3.6 с большой суммой 9999999999999999999999999.99
   3.7 с маленькой суммой 0.01
   3.8 с 0 суммой
   3.9 попытка добавить доход когда пользователь есть только в БД бота (нет в БД бэка)
4. Add expense
   4.1 базовый (с датой по умолчанию (сегодняшней) и без коммента)
   4.2 с указанной датой
   4.3 с комментом
   4.4 с суммой без копеек
   4.5 с суммой с копейками
   4.6 с большой суммой 9999999999999999999999999.99
   4.7 с маленькой суммой 0.01
   4.8 с 0 суммой
5. Report today
   5.1 в отчёте только транзакции с 00:00:00 сегодняшней даты по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно
   5.2 проценты в детализации по категориям рассчитаны верно
6. Report week
   6.1 в отчёте только транзакции с 00:00:00 (пн текущей недели) по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно
   6.2 проценты в детализации по категориям рассчитаны верно
7. Report month
   7.1 в отчёте только транзакции с 00:00:00 (1 день текущего месяца) по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно
   7.2 проценты в детализации по категориям рассчитаны верно
8. Report year
   8.1 в отчёте только транзакции с 00:00:00 (1 день текущего года) по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно
   8.2 проценты в детализации по категориям рассчитаны верно
9. Проверка баланса
   8.1 текущий баланс показывает текущий остаток пользователя (сколько сейчас по факту в кошельке)
   8.2 в графах доходы и расходы сумма с 1 числа текущего месяца по сегодня
10. 

### Links
[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)
