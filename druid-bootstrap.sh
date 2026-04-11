#!/usr/bin/env sh

set -eu

DRUID_ROUTER_URL="${DRUID_ROUTER_URL:-http://router:8888}"
DATASOURCE_NAME="${DATASOURCE_NAME:-realtime_analytics_data}"
SUPERVISOR_SPEC_PATH="${SUPERVISOR_SPEC_PATH:-/bootstrap/kafka_supervisor_spec.json}"
RETENTION_RULES_PATH="${RETENTION_RULES_PATH:-/bootstrap/realtime_analytics_data.json}"

echo "Waiting for Druid Router at ${DRUID_ROUTER_URL}..."
until curl -fsS "${DRUID_ROUTER_URL}/status/health" >/dev/null 2>&1; do
  sleep 5
done

echo "Configuring retention rules for datasource ${DATASOURCE_NAME}..."
curl -fsS -X POST "${DRUID_ROUTER_URL}/druid/coordinator/v1/rules/${DATASOURCE_NAME}" \
  -H "Content-Type: application/json" \
  --data-binary "@${RETENTION_RULES_PATH}"

echo "Configuring Kafka supervisor..."
curl -fsS -X POST "${DRUID_ROUTER_URL}/druid/indexer/v1/supervisor" \
  -H "Content-Type: application/json" \
  --data-binary "@${SUPERVISOR_SPEC_PATH}"

echo "Druid bootstrap complete."
