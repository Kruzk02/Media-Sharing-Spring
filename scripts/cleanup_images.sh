#!/bin/bash

set -e

IMAGE_DIR="../image"
KEEP_FILE="default_profile_picture.png"

echo "Cleaning up images in $IMAGE_DIR, keeping $KEEP_FILE..."

for file in "$IMAGE_DIR"/*; do
  if [[ $(basename "$file") != "$KEEP_FILE" ]]; then
      rm -f "$file"
      echo "Deleted: $(basename "$file")"
  fi
done

echo "Cleanup complete ✅"