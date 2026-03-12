import json

def generate_kotlin_map():
    with open('audio_manifest.json', 'r') as f:
        manifest = json.load(f)
    
    kotlin_code = """package com.mindfulminutes.audio

object AudioMap {
    val bundle = mapOf(
"""
    for text, res_name in manifest.items():
        # Escape double quotes in text
        escaped_text = text.replace('"', '\\"')
        kotlin_code += f'        "{escaped_text}" to "{res_name}",\n'
    
    kotlin_code += """    )
}
"""
    
    output_path = 'android/app/src/main/java/com/mindfulminutes/audio/AudioMap.kt'
    with open(output_path, 'w') as f:
        f.write(kotlin_code)
    
    print(f"Generated AudioMap.kt with {len(manifest)} entries.")

if __name__ == "__main__":
    generate_kotlin_map()
