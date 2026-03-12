import json
import re

def extract_strings(filepath):
    with open(filepath, 'r') as f:
        content = f.read()
    
    # Simple regex to find strings in lists and maps
    # This is a bit crude but should work for this specific codebase
    strings = re.findall(r'"([^"]+)"', content)
    # Filter for things that look like instructions (sentences, ending with punctuation or ellipsis)
    instructions = [s for s in strings if len(s) > 10 and (s.endswith('.') or s.endswith('\u2026') or s.endswith('!'))]
    return list(set(instructions))

files = [
    'android/app/src/main/java/com/mindfulminutes/data/ExerciseData.kt',
    'android/app/src/main/java/com/mindfulminutes/data/MoodData.kt'
]

all_instructions = []
for f in files:
    all_instructions.extend(extract_strings(f))

# Add some specific phrases
all_instructions.extend([
    "Well done. You showed up for yourself.",
    "Activity complete. well done.",
    "Breathe in slowly through your nose\u2026",
    "Hold gently\u2026",
    "Exhale slowly through your mouth\u2026",
    "Hold empty \u2014 feel the stillness\u2026"
])

manifest = {}
for i, text in enumerate(sorted(list(set(all_instructions)))):
    # Create a safe resource name
    # We use a hash or just a number for simplicity in res/raw
    safe_name = "voice_" + str(i).zfill(3)
    manifest[text] = safe_name

with open('audio_manifest.json', 'w') as f:
    json.dump(manifest, f, indent=2)

print(f"Extracted {len(manifest)} potential audio snippets.")
