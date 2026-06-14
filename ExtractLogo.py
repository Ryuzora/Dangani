import json
import base64
import re

def extract():
    transcript_path = r"C:\Users\ASUS\.gemini\antigravity\brain\b68f19c1-546b-4b56-9099-30919d14c596\.system_generated\logs\transcript_full.jsonl"
    with open(transcript_path, 'r', encoding='utf-8') as f:
        for line in f:
            if '<svg width="1020"' in line and 'data:image/png;base64,' in line:
                match = re.search(r'data:image/png;base64,([A-Za-z0-9+/=]+)', line)
                if match:
                    base64_data = match.group(1)
                    with open('app_logo_original.png', 'wb') as img_f:
                        img_f.write(base64.b64decode(base64_data))
                    print("Successfully extracted app_logo_original.png")
                    return
    print("Could not find base64 data in transcript.")

if __name__ == '__main__':
    extract()
