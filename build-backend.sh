#!/bin/bash
set -euo pipefail

REGISTRY="crpi-an6wqxpfmblx68f3.cn-beijing.personal.cr.aliyuncs.com"
IMAGE="$REGISTRY/qjhj/123"
TAG="${1:-$(date +%Y%m%d%H%M)}"
ROOT="$(cd "$(dirname "$0")" && pwd)"

docker login --username=小黑11哈哈哈 "$REGISTRY"
docker build -t "$IMAGE:$TAG" -t "$IMAGE:latest" "$ROOT/backend"
docker push "$IMAGE:$TAG"
docker push "$IMAGE:latest"
echo "后端已推送: $IMAGE:$TAG"
