#!/bin/bash

set -e

echo "🚀 Starting Grocery Store Backend (Docker)..."

if ! docker info > /dev/null 2>&1; then
  echo "⚠️ Docker is not running! Please start Docker first."
  exit 1
fi

if [ "$1" == "--build" ]; then
  echo "🔧 Rebuilding Docker images..."
  docker compose build
fi

echo "⬆️ Starting containers..."
docker compose up -d

echo "✅ All services started!"