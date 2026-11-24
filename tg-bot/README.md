# telegram-bot-kotlin
Adapter for Telegram API

1. Receive messages from Tg
2. Convert them into internal events/commands
3. Send to backend (REST API, events)
4. Receive responses from back and send it to Tg

## Разработка

### Подготовка локальной базы

Запускайте команды из корня проекта с JDK 17. Для генерации jOOQ нужна доступная PostgreSQL с применёнными миграциями.

~~~bash
docker compose -f tg-bot/bot-pg-docker-compose.yml up -d
./gradlew :tg-bot:flywayMigrate
./gradlew :tg-bot:generateJooq
~~~

В PowerShell используйте `./gradlew.bat` вместо `./gradlew`. Задачи Flyway и jOOQ берут настройки из `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` и `SPRING_DATASOURCE_PASSWORD`; их значения по умолчанию подходят для указанного compose. Для локального запуска задайте `BOT_TOKEN` и переопределите `bot.username` своим именем бота. Если бэкенд запущен по другому адресу, задайте `BUDGET_URL`. Настройки приложения описаны в [application.yml](src/main/resources/application.yml).

### Как выполняется диалог

1. [TelegramConsumer](src/main/kotlin/ru/telegram/bot/adapter/api/TelegramConsumer.kt) получает обновление. Зарегистрированная команда запускает сценарий через `TgStepMessageEvent`. Обычный ответ или нажатие inline-кнопки [ReceiverService](src/main/kotlin/ru/telegram/bot/adapter/service/ReceiverService.kt) привязывает к текущему `step_code` чата и передаёт в `TgReceivedMessageEvent` или `TgReceivedCallbackEvent`.
2. [ApplicationListener](src/main/kotlin/ru/telegram/bot/adapter/service/ApplicationListener.kt) обрабатывает `TgStepMessageEvent`: сохраняет новый шаг через `ChatContextRepository.updateUserStep` и вызывает `MessageService`. Переходы между шагами оформляйте через событие или возвращаемый `StepCode`.
3. [MessageService](src/main/kotlin/ru/telegram/bot/adapter/service/MessageService.kt) формирует и отправляет ответ через `MessageContext` и `TelegramClient`. При `botPause = false` после отправки вызывается `StepContext.next`. При `botPause = true` следующий шаг вычисляется после обработки входящего ответа через `LogicContext`.
4. [StepContext](src/main/kotlin/ru/telegram/bot/adapter/strategy/StepContext.kt) вызывает `getNextStep(chatId)` у подходящего `Step`. Возврат `null` завершает цепочку переходов. Для очистки контекста, черновика транзакции и параметров поиска верните `StepCode.FINAL`, как в `BalanceStep`.

`step_code` хранит текущую точку диалога. `flow_context` хранит сценарий, когда общий шаг используется несколькими командами. Например, `SelectCategoryStep` после выбора категории ведёт либо к вводу суммы, либо к результатам поиска.

### Соглашение об именах

[CommonUtils.currentStepCode](src/main/kotlin/ru/telegram/bot/adapter/utils/CommonUtils.kt) удаляет ожидаемый суффикс имени класса и преобразует остальную часть из PascalCase в имя [StepCode](src/main/kotlin/ru/telegram/bot/adapter/dto/enums/StepCode.kt). Для `EXAMPLE_ACTION` соответствия такие.

| Компонент | Имя | Где разместить |
| --- | --- | --- |
| Входная команда | `ExampleActionCommand` | `strategy/command/<область>` |
| Переход | `ExampleActionStep` | `strategy/stepper/<область>` |
| Ответ пользователю | `ExampleActionMessage` | `strategy/message/<область>` |
| Данные ответа | `ExampleActionRepository` | `strategy/data/<область>` |
| Обработка ввода | `ExampleActionChooser` | `strategy/logic/<область>` |
| Шаблон FreeMarker | `example_action.ftl` | `src/main/resources/message/template` |

[TelegramConfig](src/main/kotlin/ru/telegram/bot/adapter/config/TelegramConfig.kt) автоматически индексирует Spring-бины сообщений и обработчиков по `StepCode`. Для одного шага используйте по одной реализации каждого вида стратегии. Для шагов и поставщиков данных берётся первое совпадение, а для карт стратегий дублирующий ключ перезаписывается. При переименовании `StepCode` учтите его строковое хранение в `step_code` и `error_step`, а для кодов сценариев также в `flow_context`.

### Как добавить команду

1. Добавьте значение в [BotCommand](src/main/kotlin/ru/telegram/bot/adapter/dto/enums/BotCommand.kt). Поле `command` содержит имя без `/`, поле `desc` — описание. Добавьте соответствующий начальный `StepCode`.
2. Создайте `@Component` с суффиксом `Command` и наследуйте `AbstractCommand`, передав ему значение `BotCommand`. `TelegramConsumer` зарегистрирует бин при запуске. Пример простой команды — [HelpCommand](src/main/kotlin/ru/telegram/bot/adapter/strategy/command/common/HelpCommand.kt).
3. Если команде нужны аргументы или начальные данные, переопределите `doPrepare(user, chat, arguments)`. Базовый `prepare` проверяет регистрацию пользователя перед вызовом этого хука, а `execute` затем публикует начальный шаг. Например, [AddIncomeCommand](src/main/kotlin/ru/telegram/bot/adapter/strategy/command/transaction/AddIncomeCommand.kt) очищает предыдущий черновик, задаёт тип транзакции и сохраняет `flow_context`.
4. Соберите начальный шаг по инструкции ниже. Для публичной команды добавьте запись в [commands.ftl](src/main/resources/message/template/commands.ftl), при необходимости обновите [help.ftl](src/main/resources/message/template/help.ftl) и список команд в меню Telegram, если он ведётся через BotFather.

### Как добавить шаг

1. Добавьте `StepCode` и определите тип и паузу. `SEND_MESSAGE` подходит для текста и текста с кнопками. `SEND_PHOTO` использует стратегию `AbstractSendPhoto`. `NO_MESSAGE` подходит для внутреннего действия. Поставьте `botPause = true`, если нужно дождаться сообщения или callback; для автоматического перехода используйте `false`.
2. Для текстового ответа добавьте `@Component`, унаследованный от `AbstractSendMessage<T>`. По умолчанию `MessageWriter` загружает `<step_code в нижнем регистре>.ftl`. Если текст динамический и формируется в Kotlin, можно переопределить `message(data)`, как в `SelectCategoryMessage`. Для `NO_MESSAGE` стратегия ответа и шаблон не требуются.
3. Если для ответа нужны данные, создайте DTO, реализующий `DataModel`, и бин `@Repository` на основе `AbstractRepository<T>` с методом `getData(chatId)`. Он поставляет данные для текста и кнопок. В FreeMarker модель доступна как `data`, например `${data.balance}`. Для статического текста используйте `AbstractSendMessage<DataModel>` и шаблон без обращения к `data`. Пример полного набора — [BalanceRepository](src/main/kotlin/ru/telegram/bot/adapter/strategy/data/report/BalanceRepository.kt), [BalanceMessage](src/main/kotlin/ru/telegram/bot/adapter/strategy/message/report/BalanceMessage.kt) и [balance.ftl](src/main/resources/message/template/balance.ftl).
4. Для шага с переходом или выполняемым действием добавьте `@Component`, реализующий `Step`. В `getNextStep(chatId)` верните код следующего шага, `FINAL` для очистки диалога или `null` для завершения без очистки. Для статического ответа без следующих действий достаточно стратегии сообщения, как у `HELP`.
5. Добавьте вход в новый шаг из команды либо из `getNextStep` предыдущего шага. Для шага с паузой добавьте обработчик ввода.

### Как обработать текст, кнопки и ошибки

- Для текста и reply-клавиатуры реализуйте `MessageChooser` в классе `<ИмяШага>Chooser` с `@Component`. Прочитайте нужные поля из объекта Telegram `Message`, проверьте ввод и запишите результат в состояние сценария. Для reply-клавиатуры задайте кнопки через `replyButtons()`; например, текст кнопки приходит как текст сообщения.
- Для inline-клавиатуры переопределите `inlineButtons()` в стратегии сообщения. `MessageService` группирует кнопки по `rowPos` и использует `MarkupDataDto.text` одновременно как подпись и `callbackData`. Реализуйте `CallbackChooser`, проверьте `callbackQuery.data` относительно допустимых вариантов текущего шага и сохраните выбор. Пример — связка [SelectCategoryMessage](src/main/kotlin/ru/telegram/bot/adapter/strategy/message/transaction/SelectCategoryMessage.kt) и [SelectCategoryChooser](src/main/kotlin/ru/telegram/bot/adapter/strategy/logic/transaction/SelectCategoryChooser.kt).
- `ExecuteStatus.FINAL` у callback означает, что слушатель может вызвать `getNextStep`. Для `NOTHING` слушатель сейчас выбрасывает исключение. Если ошибочный callback должен показать сообщение и повторить вопрос, запишите ошибку через `ErrorService.logError`, верните `FINAL` и разрешите переход через `ErrorService.resolveNextStep`.
- Для ошибок ввода используйте подходящий подкласс `ValidationException`. Обработчик сохраняет ошибку и код текущего шага через `ErrorService.logError`, как в [EnterAmountChooser](src/main/kotlin/ru/telegram/bot/adapter/strategy/logic/transaction/EnterAmountChooser.kt). [EnterAmountStep](src/main/kotlin/ru/telegram/bot/adapter/strategy/stepper/transaction/EnterAmountStep.kt) вызывает `resolveNextStep(chatId, onSuccessStep)`. При ошибке бот отправляет сообщение шага `ERROR`, а `ErrorStep` очищает данные ошибки и возвращает пользователя к исходному шагу.

### Пример автоматического шага CreateTransactionStep

1. Пользователь попадает в `CREATE_TRANSACTION` после отказа от комментария в `AskCommentStep` или успешного ввода в `EnterCommentStep`. Категория, сумма и остальные данные к этому моменту находятся в черновике транзакции.
2. Для `CREATE_TRANSACTION` заданы `SEND_MESSAGE` и `botPause = false`, поэтому сначала отправляется сообщение об успехе из [create_transaction.ftl](src/main/resources/message/template/create_transaction.ftl), затем вызывается [CreateTransactionStep.getNextStep](src/main/kotlin/ru/telegram/bot/adapter/strategy/stepper/transaction/CreateTransactionStep.kt).
3. `CreateTransactionStep` читает черновик, вызывает `TransactionService.addTransaction` (через `TransactionClient` в `budget`) и возвращает `BALANCE`. После показа баланса `BalanceStep` переводит диалог в `FINAL` для очистки временных данных.
4. При добавлении автоматического действия, для которого нужен подтверждённый результат, используйте внутренний шаг `NO_MESSAGE` для вызова сервиса и переходите к отдельному сообщению об успехе после успешной операции. Продумайте сценарий ошибки и повторное выполнение, особенно для операций записи. В текущем `CreateTransactionStep` черновик читается через `!!`, а ошибка вызова бэкенда не переводится в `ERROR`.

### Как добавить данные диалога

- Состояние диалога хранится через `repository`, например в `ChatContextRepository`, `TransactionDraftRepository` и `SearchContextRepository`. `strategy/data` поставляет модели для отображения.
- Для изменения схемы добавьте новую версионную SQL-миграцию в [db/migration](src/main/resources/db/migration). Примените её к локальной базе и заново выполните `:tg-bot:generateJooq`, затем обновите запросы репозитория на сгенерированных типах.
- Если новый сценарий требует собственной записи на каждый чат, определите момент её создания. `AbstractCommand.prepare` создаёт существующие записи при первой регистрации, поэтому для уже зарегистрированных пользователей понадобится заполнение при миграции или создание записи при первом обращении. Для временных полей предусмотрите очистку при повторном запуске сценария и в `FinalStep`.

### Как проверить изменение

- Используйте тесты в `tg-bot/src/test` как образцы. `AddIncomeCommandTest` проверяет подготовку, `EnterAmountChooserTest` и `EnterAmountStepTest` — ввод и переход, `SelectCategoryMessageTest` — разметку, `CreateTransactionStepTest` — вызов сервиса и переход к балансу. Для выборочной проверки запускайте `./gradlew :tg-bot:test --tests '*CreateTransactionStepTest'`. Для репозиторных тестов нужен Docker с поддержкой Testcontainers.
- В локальном боте проверьте запуск команды, переходы после текста или кнопки, повтор вопроса при неверном вводе, завершение и повторный запуск сценария. Для команды, которая пишет данные, проверьте результат в бэкенде.


### Users flow
#### Общее
1. Запуск бота
   * Первый раз
   * пользователь уже зарегистрирован

#### Работа с транзакциями
1. Добавление транзакции через `/add_income` или `/add_expense`<br>
   -> выбор категории<br>
   -> ввод суммы<br>
   -> ввод даты (опционально)<br>
   -> ввод комментария (опционально)<br>
   -> сохранение транзакции и показ баланса<br>

2. Поиск транзакции<br>
   -> выбор типа транзакции<br>
   -> выбор категории<br>
   -> показ 10 последних транзакций<br>

3. Удаление транзакции<br>
   -> /delete_transaction<br>
   -> Введите номер транзакции<br>
   -> ввод pretty id транзакции<br>
   -> вывод информации о транзакции<br>
   -> удалить транзакцию? <br>
   -> кнопки Yes\No<br>
4. 

#### Отчёты
* Баланс
* Отчёт за сегодня
* Отчёт за неделю
* Отчёт за месяц
* Отчёт за год
* Отчёт за всё время (TODO)

### QA
Test cases
1. Первый старт (регистрация пользователя в БД бота, бэка, показ приветственного сообщения и help сообщения)
2. Старт существующего пользователя (есть в БД бота, бэка) -> показ баланса
3. Add income<br>
   3.1 базовый (с датой по умолчанию (сегодняшней) и без коммента) <br>
   3.2 с указанной датой<br>
   3.3 с комментом<br>
   3.4 с суммой без копеек<br>
   3.5 с суммой с копейками<br>
   3.6 с большой суммой 9999999999999999999999999.99<br>
   3.7 с маленькой суммой 0.01<br>
   3.9 попытка добавить доход когда пользователь есть только в БД бота (нет в БД бэка)<br>
   3.10 сумма <0 - ошибка<br>
   3.11 сумма >9223372036854775807 - ошибка<br>
   3.12 сумма не число (буква, спец символ) - ошибка<br>
   3.13 сумма с разделителем , - ок<br>
   3.14 сумма с пробелом в начале\в конце - ок<br>
   3.15 коммент длиннее 50 символов - ошибка<br>
   3.16 дата вида 1.1.2001 - ок<br>
   3.17 дата до 1.1.1970 - ошибка<br>
   3.18 дата в будущем - ошибка<br>
   3.19 дата вида 11.11.2001 - ок<br>
   3.20 дата с несуществующим числом месяца 30.02.2026 - ошибка<br>
   3.21 дата с несуществующим месяцем 15.13.2026 - ошибкам
4. Add expense<br>
   4.1 базовый (с датой по умолчанию (сегодняшней) и без коммента)<br>
   4.2 с указанной датой<br>
   4.3 с комментом<br>
   4.4 с суммой без копеек<br>
   4.5 с суммой с копейками<br>
   4.6 с большой суммой 9999999999999999999999999.99<br>
   4.7 с маленькой суммой 0.01<br>
   4.8 с 0 суммой<br>
5. Report today<br>
   5.1 в отчёте только транзакции с 00:00:00 сегодняшней даты по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно<br>
   5.2 проценты в детализации по категориям рассчитаны верно<br>
6. Report week<br>
   6.1 в отчёте только транзакции с 00:00:00 (пн текущей недели) по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно<br>
   6.2 проценты в детализации по категориям рассчитаны верно<br>
7. Report month<br>
   7.1 в отчёте только транзакции с 00:00:00 (1 день текущего месяца) по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно<br>
   7.2 проценты в детализации по категориям рассчитаны верно<br>
8. Report year<br>
   8.1 в отчёте только транзакции с 00:00:00 (1 день текущего года) по текущий момент, суммы по категориям и общие рассчитаны верно (доходы\расходы), итоговый баланс рассчитан верно, доля (%) категории по отношению к общему доходу\расходу рассчитан верно<br>
   8.2 проценты в детализации по категориям рассчитаны верно<br>
9. Проверка баланса<br>
   8.1 текущий баланс показывает текущий остаток пользователя (сколько сейчас по факту в кошельке)
   8.2 в графах доходы и расходы сумма с 1 числа текущего месяца по сегодня<br>
10. /delete_transaction<br>
    10.1 номер транзакции < 0 - ошибка<br>
    10.2 номер транзакции > 2147483647 - ошибка<br>
    10.3 номер транзакции не число (содержит буквы или спец символы) - ошибка<br>
    10.4 транзакции с таким номером не существует - ошибка<br>
    10.5 введён верный номер транзакции - транзакция удалена (не отображается при поиске)<br>
    10.6 при удалении транзакции когда транзакции не найдены, не предлагает удалить (Удалить транзакцию? Yes/No)<br>
11. /search_transactions

### Links
[This tg-bot template doc](https://habr.com/ru/articles/588474/)
[Tg-bot official doc](https://tlgrm.ru/docs/bots)
[Tg-bot API guide](https://tlgrm.ru/docs/bots/api)
