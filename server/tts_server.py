from fastapi import FastAPI, Response
from fastapi.responses import FileResponse
import subprocess
import os
import hashlib

app = FastAPI()

# Configuration
PIPER_EXE = "piper" # Path to piper executable
MODEL_PATH = "en_US-lessac-medium.onnx" # Path to voice model
CACHE_DIR = "cache"

if not os.path.exists(CACHE_DIR):
    os.makedirs(CACHE_DIR)

@app.get("/tts")
def tts(text: str):
    # Create a hash for the text to use as filename (cache)
    filename = hashlib.md5(text.encode()).hexdigest() + ".wav"
    filepath = os.path.join(CACHE_DIR, filename)

    if not os.path.exists(filepath):
        # Run piper command
        # Syntax: echo "text" | piper --model model.onnx --output_file file.wav
        try:
            command = f'echo "{text}" | {PIPER_EXE} --model {MODEL_PATH} --output_file {filepath}'
            subprocess.run(command, shell=True, check=True)
        except Exception as e:
            return {"error": str(e)}

    return FileResponse(filepath, media_type="audio/wav")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
