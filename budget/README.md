# Backend for budget App
Contains business logic

1. Receive requests from tg-bot adapter
2. Works with database layer
3. Works with integrations

## Database
Postgres start: [docker-compose.yml](docker-compose.yml)

Migrations: [changelog-001-create-tables.xml](src%2Fmain%2Fresources%2Fdb%2Fchangelog%2Fchangelog-001-create-tables.xml)

docker exec -it {CONTAINER_ID} psql -U {PSQL_USER} -d {DNB_NAME} -c "SELECT * FROM {TABLE};"

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
