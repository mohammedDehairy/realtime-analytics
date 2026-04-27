#!/usr/bin/env bash

set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEFAULT_ENV_FILE="${ROOT_DIR}/.env.staging"
ENV_FILE="${ENV_FILE:-${DEFAULT_ENV_FILE}}"

usage() {
  cat >&2 <<'EOF'
Usage: deploy-cloud-run-image.sh [--env-file <path>]

Required env values:
  GCP_REGION
  GCP_PROJECT_ID
  GCP_ARTIFACT_REPOSITORY
  IMAGE_NAME
  IMAGE_TAG
  CLOUD_RUN_SERVICE
  CLOUD_RUN_SERVICE_ACCOUNT
  JWT_SECRET_NAME
  DEVICE_ID_SECRET_NAME
  API_KEY_SECRET_NAME
  TINYBIRD_TOKEN_SECRET_NAME
  TINYBIRD_EVENTS_URL
  TINYBIRD_DATASOURCE
EOF
}

require_command() {
  local command_name="$1"
  if ! command -v "${command_name}" >/dev/null 2>&1; then
    echo "Missing required command: ${command_name}" >&2
    exit 1
  fi
}

require_env() {
  local env_name="$1"
  if [[ -z "${!env_name:-}" ]]; then
    echo "Missing required environment variable: ${env_name}" >&2
    exit 1
  fi
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
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage
      exit 1
      ;;
  esac
done

require_command gcloud
load_env_file "${ENV_FILE}"

required_env_vars=(
  GCP_REGION
  GCP_PROJECT_ID
  GCP_ARTIFACT_REPOSITORY
  IMAGE_NAME
  IMAGE_TAG
  CLOUD_RUN_SERVICE
  CLOUD_RUN_SERVICE_ACCOUNT
  JWT_SECRET_NAME
  DEVICE_ID_SECRET_NAME
  API_KEY_SECRET_NAME
  TINYBIRD_TOKEN_SECRET_NAME
  TINYBIRD_EVENTS_URL
  TINYBIRD_DATASOURCE
)

for env_var in "${required_env_vars[@]}"; do
  require_env "${env_var}"
done

IMAGE_REF="${GCP_REGION}-docker.pkg.dev/${GCP_PROJECT_ID}/${GCP_ARTIFACT_REPOSITORY}/${IMAGE_NAME}:${IMAGE_TAG}"
SERVICE_ACCOUNT_MEMBER="serviceAccount:${CLOUD_RUN_SERVICE_ACCOUNT}"

gcloud config set project "${GCP_PROJECT_ID}"

gcloud secrets add-iam-policy-binding "${JWT_SECRET_NAME}" \
  --member="${SERVICE_ACCOUNT_MEMBER}" \
  --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding "${DEVICE_ID_SECRET_NAME}" \
  --member="${SERVICE_ACCOUNT_MEMBER}" \
  --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding "${API_KEY_SECRET_NAME}" \
  --member="${SERVICE_ACCOUNT_MEMBER}" \
  --role="roles/secretmanager.secretAccessor"

gcloud secrets add-iam-policy-binding "${TINYBIRD_TOKEN_SECRET_NAME}" \
  --member="${SERVICE_ACCOUNT_MEMBER}" \
  --role="roles/secretmanager.secretAccessor"

gcloud run deploy "${CLOUD_RUN_SERVICE}" \
  --image "${IMAGE_REF}" \
  --region "${GCP_REGION}" \
  --platform managed \
  --allow-unauthenticated \
  --set-env-vars "EVENT_PUBLISHER_BACKEND=tinybird,TINYBIRD_EVENTS_URL=${TINYBIRD_EVENTS_URL},TINYBIRD_DATASOURCE=${TINYBIRD_DATASOURCE}" \
  --set-secrets "JWT_SECRET_KEY=${JWT_SECRET_NAME}:latest,DEVICE_ID_SECRET_KEY=${DEVICE_ID_SECRET_NAME}:latest,API_SECURITY_APPLICATION_KEY_IMOTION=${API_KEY_SECRET_NAME}:latest,TINYBIRD_TOKEN=${TINYBIRD_TOKEN_SECRET_NAME}:latest"
