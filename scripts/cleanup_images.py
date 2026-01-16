import os
import sys

IMAGE_DIR = "../image"
KEEP_FILE = "default_profile_picture.png"

print(f"Cleaning up images in {IMAGE_DIR}, keeping {KEEP_FILE}")

if not os.path.isdir(IMAGE_DIR):
    print(f"Error: Directory not found {IMAGE_DIR}")
    sys.exit(1)

for filename in os.listdir(IMAGE_DIR):
    if filename != KEEP_FILE:
        file_path = os.path.join(IMAGE_DIR, filename)

        if os.path.isfile(file_path):
            os.remove(file_path)
            print(f"Deleted: {filename}")

print("Cleanup complete")
