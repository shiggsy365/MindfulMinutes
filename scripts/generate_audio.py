import json
import os
import asyncio
import edge_tts

# Using Edge-TTS which is FREE, high-quality, and very stable.
# No API key required!
MANIFEST_PATH = "audio_manifest.json"
OUTPUT_DIR = "android/app/src/main/res/raw"
VOICE = "en-GB-SoniaNeural" # A very calm, high-quality neural voice

if not os.path.exists(OUTPUT_DIR):
    os.makedirs(OUTPUT_DIR)

async def generate_voice(text, filename):
    filepath = os.path.join(OUTPUT_DIR, f"{filename}.mp3")
    
    if os.path.exists(filepath):
        return

    print(f"Generating: {filename} - {text[:30]}...")
    
    try:
        communicate = edge_tts.Communicate(text, VOICE)
        await communicate.save(filepath)
    except Exception as e:
        print(f"Failed to generate {filename}: {e}")

async def main():
    with open(MANIFEST_PATH, 'r') as f:
        manifest = json.load(f)

    print(f"Starting Edge-TTS generation for {len(manifest)} files...")
    
    # Process in chunks to avoid overwhelming the server
    chunk_size = 10
    items = list(manifest.items())
    
    for i in range(0, len(items), chunk_size):
        chunk = items[i:i + chunk_size]
        tasks = [generate_voice(text, filename) for text, filename in chunk]
        await asyncio.gather(*tasks)
        print(f"Progress: {min(i + chunk_size, len(items))}/{len(items)}")

if __name__ == "__main__":
    asyncio.run(main())
