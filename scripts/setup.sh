#!/bin/bash

set -e

function detect_os() {
  case "$(uname -s | tr '[:upper:]' '[:lower:]')" in
    linux*)   echo "Linux" ;;
    darwin*)  echo "macOS" ;;
    msys*|mingw*|cygwin*) echo "Windows" ;;
    *)        echo "Unknown" ;;
  esac
}

function check_docker() {

if command -v docker &> /dev/null; then
    echo "✅ Docker is already installed. Consider updating it if needed."
else
    echo "❌ Docker is not installed."

    case "$OS_NAME" in
        "Linux")
            echo "👉 Please install Docker using your package manager."
            echo "For example (Ubuntu/Debian):"
            echo "    sudo apt update && sudo apt install -y docker.io"
            ;;
        "macOS")
            echo "👉 Please install Docker Desktop from the official website:"
            echo "    https://docs.docker.com/desktop/setup/install/mac-install/"
            ;;
        "Windows")
            echo "👉 Downloading Docker Desktop installer for Windows..."
            curl -L -o DockerInstaller.exe "https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe"

            echo "📦 Running Docker Desktop installer..."
            cmd.exe /c start DockerInstaller.exe

            echo "🧹 Cleaning up installer..."

            rm DockerInstaller.exe
            ;;
        *)
            echo "⚠️ Unsupported or unrecognized OS. Please install Docker manually from:"
            echo "    https://docs.docker.com/desktop/"
            ;;
    esac
fi
}
function check_java() {

if command -v java >/dev/null 2>&1; then
    echo "✅ Java is already installed. Consider updating it if needed."
else
  echo "❌ Java is not installed."

    case "$OS_NAME" in
      "Linux")
          echo "👉 Please install Java using your package manager."
          echo "For example (Ubuntu/Debian):"
          echo "    sudo apt update && sudo apt install -y openjdk-24-jdk"
          echo
          echo "For Fedora/CentOS/RHEL:"
          echo "    sudo dnf install -y java-24-openjdk-devel"
          echo
          echo "For Arch Linux:"
          echo "    sudo pacman -S jdk24-openjdk"
          ;;

      "macOS")
          echo "👉 Please install Java from the official website or use Homebrew."
          echo
          echo "Using Homebrew (recommended):"
          echo "    brew install openjdk@24"
          echo "    sudo ln -sfn /usr/local/opt/openjdk@24/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-24.jdk"
          echo
          echo "Or download manually from Oracle:"
          echo "    https://www.oracle.com/java/technologies/downloads/"
          ;;

      "Windows")
          echo "👉 Downloading Java installer for Windows..."
          curl -L -o jdk-installer.exe "https://download.oracle.com/java/24/latest/jdk-24_windows-x64_bin.exe"

          echo "📦 Running Java installer..."
          cmd.exe /c start jdk-installer.exe

          echo "🧹 Cleaning up installer..."
          rm jdk-installer.exe
          ;;

      *)
          echo "⚠️ Unsupported or unrecognized OS. Please install Java manually from:"
          echo "    https://www.oracle.com/java/technologies/downloads/"
          ;;
    esac
fi
}

function set_env() {
    if [ ! -f ../.env ]; then
        cp ../.example.env ../.env
        echo "✅ Created .env from template."
    else
        echo "ℹ️ .env already exists — skipping."
    fi
}

function main() {
  OS_NAME=$(detect_os)
  echo "Detected OS: $OS_NAME"

  check_docker
  check_java
  set_env
  echo -e "\n ✅ Setup complete for $OS_NAME!"
}

main "$@"
