#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 <full|tinybird> [down]" >&2
  exit 1
fi

STACK_MODE="$1"
STACK_ACTION="${2:-up}"

case "${STACK_MODE}" in
  full)
    COMPOSE_FILE="docker-compose.yml"
    ;;
  tinybird)
    COMPOSE_FILE="docker-compose.tinybird.yml"
    ;;
  *)
    echo "Unknown stack mode: ${STACK_MODE}" >&2
    echo "Usage: $0 <full|tinybird> [down]" >&2
    exit 1
    ;;
esac

case "${STACK_ACTION}" in
  up|down)
    ;;
  *)
    echo "Unknown stack action: ${STACK_ACTION}" >&2
    echo "Usage: $0 <full|tinybird> [down]" >&2
    exit 1
    ;;
esac

require_command() {
  local command_name="$1"
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "Missing required command: ${command_name}" >&2
    exit 1
  fi
}

log_step() {
  echo
  echo "==> $1"
}

run_maven_module() {
  local module_dir="$1"
  log_step "Building ${module_dir}"
  (
    cd "${ROOT_DIR}/${module_dir}"
    mvn -U clean install
  )
}

require_command mvn
require_command docker

if [[ "${STACK_ACTION}" == "down" ]]; then
  log_step "Stopping Docker Compose stack (${COMPOSE_FILE})"
  (
    cd "${ROOT_DIR}"
    docker compose -f "${COMPOSE_FILE}" down --remove-orphans
  )

  echo
  echo "Done."
  exit 0
fi

log_step "Stopping existing Docker Compose stack (${COMPOSE_FILE})"
(
  cd "${ROOT_DIR}"
  docker compose -f "${COMPOSE_FILE}" down --remove-orphans
)

run_maven_module "realtime-analytics-service"

log_step "Rebuilding Docker images (${COMPOSE_FILE})"
(
  cd "${ROOT_DIR}"
  docker compose -f "${COMPOSE_FILE}" build --no-cache
)

log_step "Starting Docker Compose stack (${COMPOSE_FILE})"
(
  cd "${ROOT_DIR}"
  docker compose -f "${COMPOSE_FILE}" up -d
)

log_step "Current container status"
(
  cd "${ROOT_DIR}"
  docker compose -f "${COMPOSE_FILE}" ps
)

echo
echo "Done."
