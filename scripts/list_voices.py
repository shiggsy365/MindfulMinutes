import asyncio
import edge_tts

async def main():
    voices = await edge_tts.VoicesManager.create()
    # Filter for English voices to keep the list manageable
    english_voices = [v for v in voices.find(Locale="en-US")]
    
    print(f"{'Short Name':<30} | {'Gender':<10} | {'Suggested Use'}")
    print("-" * 60)
    for v in english_voices:
        # Simple categorization for user friendliness
        usage = "General"
        if "Neural" in v["ShortName"]:
            usage = "High Quality"
        if "Guy" in v["ShortName"] or "Andrew" in v["ShortName"]:
            usage = "Deep/Calm"
        if "Aria" in v["ShortName"] or "Emma" in v["ShortName"]:
            usage = "Soft/Meditation"
            
        print(f"{v['ShortName']:<30} | {v['Gender']:<10} | {usage}")

if __name__ == "__main__":
    asyncio.run(main())
