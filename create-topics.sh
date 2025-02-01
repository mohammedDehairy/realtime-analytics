#!/bin/bash

# until nc -z kafka 9092; do
#   sleep 1
# done

docker exec kafka kafka-topics --bootstrap-server kafka:9092 --list

docker exec kafka kafka-topics --bootstrap-server kafka:9092 --create --topic realtime-analytics --partitions 1 --replication-factor 1

docker exec kafka kafka-topics --bootstrap-server kafka:9092 --list