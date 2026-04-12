#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

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

log_step "Stopping existing Docker Compose stack"
(
  cd "${ROOT_DIR}"
  docker compose down --remove-orphans
)

run_maven_module "realtime-analytics-service"

log_step "Rebuilding Docker images"
(
  cd "${ROOT_DIR}"
  docker compose build --no-cache
)

log_step "Starting Docker Compose stack"
(
  cd "${ROOT_DIR}"
  docker compose up -d
)

log_step "Current container status"
(
  cd "${ROOT_DIR}"
  docker compose ps
)

echo
echo "Done."
