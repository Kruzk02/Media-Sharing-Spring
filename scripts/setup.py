import os
import platform
import shutil
import subprocess


def check_docker(os_name: str):
    if shutil.which("docker"):
        result = subprocess.run(
            ["docker", "--version"], capture_output=True, text=True, check=True
        )
        print(result.stdout)
    else:
        print("Docker not installed.")

        if os_name == "Windows":
            print(
                "Please install Docker desktop form the offical website: https://docs.docker.com/desktop/setup/install/windows-install/"
            )
        elif os_name == "Linux":
            print("Please install docker using your package manager.")
            print(
                "For example (Ubuntu/Debain): sudo apt update && sudo apt install -y docker.io"
            )
        elif os_name == "Darwin":
            print(
                "Please install Docker Desktop from the official website: https://docs.docker.com/desktop/setup/install/mac-install/"
            )
        else:
            print(
                "Unsupported or unrecognized OS. Please install Docker manually from: https://docs.docker.com/desktop/"
            )


def check_java():
    if shutil.which("java"):
        result = subprocess.run(
            ["java", "--version"], capture_output=True, text=True, check=True
        )
        print(result.stdout)
    else:
        print(
            "Please install Java from the official website: https://www.oracle.com/java/technologies/downloads/"
        )


def set_env():
    if not os.path.isfile("../.env"):
        shutil.copy("../.example.env", "../.env")
        print("✅ Created .env from template.")
    else:
        print("ℹ️ .env already exists — skipping.")


if __name__ == "__main__":

    os_name = platform.system()
    print(f"Operating system name: {os_name}\n")

    check_docker(os_name)
    check_java()
    set_env()
    print(f"Setup complete for {os_name}")
