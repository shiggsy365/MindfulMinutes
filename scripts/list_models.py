from google import genai
import os

API_KEY = os.environ.get("GEMINI_API_KEY", "your_gemini_api_key_here")
client = genai.Client(api_key=API_KEY)

print("Listing available models...")
try:
    for m in client.models.list():
        print(f"Model: {m.name}, Multi-modal: {m.supported_generation_methods}")
except Exception as e:
    print(f"Error listing models: {e}")
