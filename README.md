# Getting Started

### Create and run Kafka docker container
```
docker compose up -d --build kafka
```
### Create a Kafka Topic "events"
```
docker exec -it kafka-broker /opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka-broker:9092 --create --topic events
```
### Create and run Postgres and Redis docker containers
```
docker compose up -d --build postgres-db redis
```
### Create and run backend docker container with env variables
```
GITHUB_CLIENT_ID=Iv23liXYJkyySjtr9gzf GITHUB_CLIENT_SECRET=68da4c8ebde981f59b5375cff223cdf75139c55d docker compose up --build -d api
```