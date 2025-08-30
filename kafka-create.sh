#!/bin/sh
set -e

# Wait until Kafka broker is reachable
echo "Waiting for Kafka broker..."
while ! nc -z kafka 9092; do
  echo "Kafka broker not ready, sleeping 2s..."
  sleep 2
done

# Create topic if it doesn't exist
echo "Ensuring Kafka topic exists..."
kafka-topics.sh \
  --bootstrap-server kafka-broker:9092 \
  --create \
  --topic events \
  --partitions 1 \
  --replication-factor 1 || echo "Topic already exists"

# Start Spring Boot
echo "Starting Spring Boot application..."
exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar packaged-app.jar
