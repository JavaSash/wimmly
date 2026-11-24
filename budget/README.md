# Backend for budget App
Contains business logic

1. Receive requests from tg-bot adapter
2. Works with database layer
3. Works with integrations

## Расширение бэкенда

### Как добавить категорию транзакций

1. Добавьте значение с описанием в `IncomeCategory` или `ExpenseCategory` в [Category.kt](src/main/kotlin/ru/wimme/logic/model/transaction/Category.kt). [CategoryApi](src/main/kotlin/ru/wimme/logic/api/CategoryApi.kt) выдаёт имена значений как коды. [ReportService](src/main/kotlin/ru/wimme/logic/service/ReportService.kt) подставляет описание по паре «тип транзакции и код».
2. Синхронизируйте резервный список соответствующего типа в [SelectCategoryRepository](../tg-bot/src/main/kotlin/ru/telegram/bot/adapter/strategy/data/transaction/SelectCategoryRepository.kt). Он используется, если бэкенд не вернул список. В штатном сценарии кнопки строятся по ответу API.
3. Проверьте выбор новой категории, сохранение и поиск транзакции, а также отображение в отчёте. Код хранится строкой в записи транзакции, поэтому для переименования существующего значения продумайте преобразование ранее сохранённых кодов и совместимость с ботом.

## Database
Postgres start: [budget-pg-docker-compose.yml](budget-pg-docker-compose.yml)

Migrations: [db.changelog-master.xml](src/main/resources/db/changelog/db.changelog-master.xml)

`docker exec -it {CONTAINER_ID} psql -U {PSQL_USER} -d {DNB_NAME} -c "SELECT * FROM {TABLE};"`
Transactions count by user
`SELECT
user_id,
COUNT(*) AS transaction_count
FROM transactions
GROUP BY user_id
ORDER BY transaction_count DESC;`

## Build docker img and push to docker hub
https://hub.docker.com
1. cd {MODULE_NAME}
2. ./gradlew clean {MODULE_NAME}:bootJar
3. cd ..
4. docker build -t {DOCKER_USER}/budget:0.1.0 ./budget
5. docker images
6. docker login -u {DOCKER_USER} -> access token
7. docker push {DOCKER_USER}/budget:0.1.0

## Deploy
1. ssh {user}@{IP}
2. docker compose up -d (all backend)
