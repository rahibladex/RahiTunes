import subprocess
import time
import sys

def run(cmd):
    p = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return p.stdout

def screenshot(name):
    path = f"/home/rahibladex/.gemini/antigravity-ide/brain/a0c3970b-1988-4535-952d-84d727e62725/{name}"
    run(f"/home/rahibladex/Android/Sdk/platform-tools/adb -s emulator-5554 shell screencap -p /sdcard/{name}")
    run(f"/home/rahibladex/Android/Sdk/platform-tools/adb -s emulator-5554 pull /sdcard/{name} {path}")
    print(f"Screenshot saved to {path}")

def tap(x, y):
    run(f"/home/rahibladex/Android/Sdk/platform-tools/adb -s emulator-5554 shell input tap {x} {y}")
    print(f"Tapped ({x}, {y})")

if __name__ == "__main__":
    if len(sys.argv) > 1 and sys.argv[1] == "tap":
        tap(sys.argv[2], sys.argv[3])
        time.sleep(1)
        if len(sys.argv) > 4:
            screenshot(sys.argv[4])
    elif len(sys.argv) > 1 and sys.argv[1] == "screenshot":
        screenshot(sys.argv[2])
