import zipfile
import sys

def strip_dex(input_apk, output_apk):
    print(f"Reading from {input_apk} and writing to {output_apk}...")
    with zipfile.ZipFile(input_apk, 'r') as yin:
        with zipfile.ZipFile(output_apk, 'w') as yout:
            for item in yin.infolist():
                if not item.filename.endswith('.dex'):
                    yout.writestr(item, yin.read(item.filename))
    print("Stripped APK created successfully.")

if __name__ == "__main__":
    strip_dex('.build-outputs/app-debug.apk', 'resources_only.apk')
