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
1. Create new class NewCommand extended from [AbstractCommand.kt](src%2Fmain%2Fkotlin%2Fru%2Ftelegram%2Fbot%2Fadapter%2Fstrategy%2Fcommand%2FAbstractCommand.kt)
2. Add value for new command in [BotCommand.kt](src%2Fmain%2Fkotlin%2Fru%2Ftelegram%2Fbot%2Fadapter%2Fdto%2Fenums%2FBotCommand.kt)
3. Pass new enum value to NewCommand class as first param
4. Create class for new message in package [message](src%2Fmain%2Fkotlin%2Fru%2Ftelegram%2Fbot%2Fadapter%2Fstrategy%2Fmessage) 
5. Create .ftl file with response txt

## TODO
1. сделать все нужные операции (под апи бэка)
2. Упростить/удалить стратегии - они переедут в бэкенд
3. Добавить клиент REST API для связи с бэкендом
4. Модифицировать слушателей для отправки запросов в бэкенд
5. Оставить только преобразование Telegram → внутренний формат

### UI
[💰 БАЛАНС: 25 430 ₽]
━━━━━━━━━━━━━━━━━━━━
📊 Сегодня:
• Доходы: +5 000 ₽
• Расходы: -2 300 ₽
━━━━━━━━━━━━━━━━━━━━

[📥 Добавить доход] [📤 Добавить расход]
[📊 Статистика]     [⚙️ Настройки]
Кнопки (Inline Keyboard) для быстрого доступа


Добавление дохода/расхода (Многошаговый диалог)

Пользователь: Нажимает "📥 Добавить доход"
Бот: "Выберите категорию дохода:"
[💼 Зарплата] [🎁 Подарок] [📈 Инвестиции] [✏️ Другое]

Пользователь: Нажимает "💼 Зарплата"  
Бот: "Введите сумму:"
[1000] [5000] [10000] [✏️ Ввести]

Пользователь: Вводит "15000"
Бот: "📅 Дата: 15 марта 2024 (сегодня)
✅ Сохранить | ✏️ Изменить дату"

Реализация:
Кнопки для выбора категорий
Быстрые кнопки для сумм
Inline кнопки для подтверждения

Просмотр статистики
Пользователь: Нажимает "📊 Статистика"
Бот: "Выберите период:"
[📅 Сегодня] [📆 Неделя] [🗓️ Месяц] [📊 Год]

Пользователь: Нажимает "🗓️ Месяц"
Бот: Отправляет график/таблицу

fun formatBalanceResponse(balanceData: BalanceData): String {
return """
*💰 БАЛАНС: ${formatCurrency(balanceData.total)}*
━━━━━━━━━━━━━━━━━━━━
📊 *${balanceData.period}*
📈 Доходы: *+${formatCurrency(balanceData.income)}*
📉 Расходы: *-${formatCurrency(balanceData.expense)}*
━━━━━━━━━━━━━━━━━━━━
💳 Наличные: ${formatCurrency(balanceData.cash)}
🏦 Карта: ${formatCurrency(balanceData.card)}
📱 МБ: ${formatCurrency(balanceData.mobile)}

    📈 Изменение за день: ${formatCurrency(balanceData.dailyChange)} (${balanceData.dailyChangePercent}%)
    """.trimIndent()
}

// Пример вывода:
// 💰 БАЛАНС: 25 430 ₽
// ━━━━━━━━━━━━━━━━━━━━
// 📊 Сегодня
// 📈 Доходы: +5 000 ₽
// 📉 Расходы: -2 300 ₽


### Links
[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)