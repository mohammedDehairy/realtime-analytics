#!/bin/bash


until kafka-topics --bootstrap-server kafka:9092 --list | grep -q "realtime-analytics"; do
  sleep 1
done

echo "kafka topic created, done waiting"