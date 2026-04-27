#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_ENV_FILE="${ROOT_DIR}/.env.staging"

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 [up|down|push] [--env-file <path>] [--image-ref <image-ref>]" >&2
  exit 1
fi

if [[ $# -gt 0 && "$1" =~ ^(up|down|push)$ ]]; then
  STACK_ACTION="$1"
  shift
else
  STACK_ACTION="up"
fi

ENV_FILE=""
IMAGE_REF="${IMAGE_REF:-}"

COMPOSE_FILE="docker-compose.tinybird.yml"

case "${STACK_ACTION}" in
  up|down|push)
    ;;
  *)
    echo "Unknown stack action: ${STACK_ACTION}" >&2
    echo "Usage: $0 [up|down|push] [--env-file <path>] [--image-ref <image-ref>]" >&2
    exit 1
    ;;
esac

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      if [[ $# -lt 2 ]]; then
        echo "Missing value for --env-file" >&2
        exit 1
      fi
      ENV_FILE="$2"
      shift 2
      ;;
    --image-ref)
      if [[ $# -lt 2 ]]; then
        echo "Missing value for --image-ref" >&2
        exit 1
      fi
      IMAGE_REF="$2"
      shift 2
      ;;
    *)
      echo "Unknown argument: $1" >&2
      echo "Usage: $0 [up|down|push] [--env-file <path>] [--image-ref <image-ref>]" >&2
      exit 1
      ;;
  esac
done

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

load_env_file() {
  local env_file="$1"
  if [[ ! -f "${env_file}" ]]; then
    echo "Env file not found: ${env_file}" >&2
    exit 1
  fi

  set -a
  # shellcheck disable=SC1090
  source "${env_file}"
  set +a
}

build_image_ref_from_env() {
  local region="${GCP_REGION:-${ARTIFACT_REGISTRY_REGION:-}}"
  local project_id="${GCP_PROJECT_ID:-${GOOGLE_CLOUD_PROJECT:-${GCLOUD_PROJECT:-}}}"
  local repository="${GCP_ARTIFACT_REPOSITORY:-${ARTIFACT_REGISTRY_REPOSITORY:-}}"
  local image_name="${IMAGE_NAME:-realtime-analytics-api}"
  local image_tag="${IMAGE_TAG:-latest}"

  if [[ -z "${region}" || -z "${project_id}" || -z "${repository}" ]]; then
    echo "Missing image settings. Set GCP_REGION, GCP_PROJECT_ID, and GCP_ARTIFACT_REPOSITORY in .env or pass --image-ref." >&2
    exit 1
  fi

  echo "${region}-docker.pkg.dev/${project_id}/${repository}/${image_name}:${image_tag}"
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

if [[ -z "${ENV_FILE}" && -f "${DEFAULT_ENV_FILE}" ]]; then
  ENV_FILE="${DEFAULT_ENV_FILE}"
fi

if [[ -n "${ENV_FILE}" ]]; then
  load_env_file "${ENV_FILE}"
fi

IMAGE_REF="$(build_image_ref_from_env)"

export IMAGE_REF

COMPOSE_ARGS=()
if [[ -n "${ENV_FILE}" ]]; then
  COMPOSE_ARGS+=(--env-file "${ENV_FILE}")
fi

if [[ "${STACK_ACTION}" == "down" ]]; then
  log_step "Stopping Docker Compose stack (${COMPOSE_FILE})"
  (
    cd "${ROOT_DIR}"
    docker compose "${COMPOSE_ARGS[@]}" -f "${COMPOSE_FILE}" down --remove-orphans
  )

  echo
  echo "Done."
  exit 0
fi

log_step "Stopping existing Docker Compose stack (${COMPOSE_FILE})"
(
  cd "${ROOT_DIR}"
  docker compose "${COMPOSE_ARGS[@]}" -f "${COMPOSE_FILE}" down --remove-orphans
)

run_maven_module "realtime-analytics-service"

log_step "Rebuilding Docker images (${COMPOSE_FILE})"
(
  cd "${ROOT_DIR}"
  docker compose "${COMPOSE_ARGS[@]}" -f "${COMPOSE_FILE}" build --no-cache
)

if [[ "${STACK_ACTION}" == "push" ]]; then
  log_step "Pushing Docker image (${IMAGE_REF})"
  (
    cd "${ROOT_DIR}"
    DOCKER_DEFAULT_PLATFORM=linux/amd64 docker compose "${COMPOSE_ARGS[@]}" -f "${COMPOSE_FILE}" push realtime-analytics-service
  )

  echo
  echo "Pushed image: ${IMAGE_REF}"
  exit 0
fi

log_step "Starting Docker Compose stack (${COMPOSE_FILE})"
(
  cd "${ROOT_DIR}"
  docker compose "${COMPOSE_ARGS[@]}" -f "${COMPOSE_FILE}" up -d
)

log_step "Current container status"
(
  cd "${ROOT_DIR}"
  docker compose "${COMPOSE_ARGS[@]}" -f "${COMPOSE_FILE}" ps
)

echo
echo "Done."
