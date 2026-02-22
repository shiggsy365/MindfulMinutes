package com.mindfulminutes.data

import androidx.compose.ui.graphics.Color

data class Mood(
    val id: String,
    val name: String,
    val emoji: String,
    val color: Color,
    val bg: Color,
    val border: Color,
    val message: String,
    val sessions: List<MoodSession>
)

data class MoodSession(
    val id: String,
    val length: String,
    val label: String,
    val mins: Int,
    val steps: List<String>
)

data class LengthMeta(
    val label: String,
    val icon: String,
    val desc: String
)

data class TrackerMood(
    val id: String,
    val emoji: String,
    val label: String,
    val score: Int,
    val color: Color
)

data class TimeSlot(
    val id: String,
    val label: String,
    val icon: String,
    val hours: String
)

val LEN_META = mapOf(
    "quick" to LengthMeta("Quick Fix", "⚡", "1–2 min"),
    "medium" to LengthMeta("Steady Calm", "\uD83C\uDF24\uFE0F", "5 min"),
    "deep" to LengthMeta("Deep Dive", "\uD83C\uDF0A", "10 min")
)

val TRACKER_MOODS = listOf(
    TrackerMood("great", "\uD83D\uDE0A", "Great", 5, Color(150, 200, 150, 230)),
    TrackerMood("good", "\uD83D\uDE42", "Good", 4, Color(170, 195, 140, 230)),
    TrackerMood("okay", "\uD83D\uDE10", "Okay", 3, Color(200, 190, 130, 230)),
    TrackerMood("low", "\uD83D\uDE14", "Low", 2, Color(190, 165, 140, 230)),
    TrackerMood("rough", "\uD83D\uDE22", "Rough", 1, Color(170, 150, 180, 230))
)

val TIME_SLOTS = listOf(
    TimeSlot("morning", "Morning", "\uD83C\uDF05", "6am – 12pm"),
    TimeSlot("afternoon", "Afternoon", "☀\uFE0F", "12pm – 6pm"),
    TimeSlot("evening", "Evening", "\uD83C\uDF19", "6pm – 12am")
)

val MOODS = listOf(
    Mood("anxious", "Anxious", "\uD83D\uDE30", Color(130, 175, 210, 230), Color(130, 175, 210, 20), Color(130, 175, 210, 51), "Anxiety is just energy looking for direction.", listOf(
        MoodSession("anx-q", "quick", "Quick Calm", 2, listOf("Place both feet flat on the ground.", "Inhale 4, hold 4, exhale 6. Repeat 3x.", "Name 5 things you can see.", "Feel body weight. You are anchored.", "'This feeling is temporary. I am safe.'")),
        MoodSession("anx-m", "medium", "Worry Unwinding", 5, listOf("Anxious thoughts as tangled threads.", "Choose the loudest worry.", "Is it about now or the future?", "Breathe in calm, breathe out the thread.", "Pick the next. Examine, release.", "Continue until the tangle loosens.", "Hand on chest. Feel it slow.", "Worries are lighter now.")),
        MoodSession("anx-d", "deep", "Safe Harbour", 10, listOf("Settle in. Body heavy.", "10 slow breaths, exhale twice as long.", "A peaceful harbour entrance.", "Still water. Boats rock gently.", "Walk the harbour wall.", "Each step, name a worry, leave it.", "Sit at the end. Calm water.", "Horizon stretches infinitely.", "Breathe with gentle waves.", "Scan body, release tension.", "Walk back. Thoughts have faded.", "Carry only the stillness."))
    )),
    Mood("stressed", "Stressed", "\uD83D\uDE24", Color(210, 160, 140, 230), Color(210, 160, 140, 20), Color(210, 160, 140, 51), "Let's set some of it down.", listOf(
        MoodSession("str-q", "quick", "Pressure Valve", 2, listOf("Clench fists tight. Hold 5s. Release.", "Shoulders to ears. Hold. Drop.", "Enormous inhale... sigh it all out.", "Tension versus relief.", "Repeat. More released than you realise.")),
        MoodSession("str-m", "medium", "Load Lightening", 5, listOf("Heavy backpack on shoulders.", "Each stone weighs on you.", "First stone out. Name it. Set down.", "Next stone. Name it. Set down.", "Continue until lighter.", "Stand taller. Roll shoulders.", "Three deep breaths in new space.", "Pick up later. Enjoy lighter.")),
        MoodSession("str-d", "deep", "Mountain Stillness", 10, listOf("Five settling breaths.", "Relax: face, jaw, neck, shoulders.", "Through chest, belly, hips, legs, feet.", "You are a great mountain.", "Stress is weather: clouds, rain, wind.", "Mountain doesn't fight weather.", "Feel storm around you. Don't resist.", "Simply observe. Unchanged.", "Storm quiets. Sun breaks through.", "Warmth returns.", "Feel it seep into your body.", "Carry the mountain's strength."))
    )),
    Mood("sad", "Sad", "\uD83D\uDE22", Color(150, 165, 200, 230), Color(150, 165, 200, 20), Color(150, 165, 200, 51), "Be gentle with yourself.", listOf(
        MoodSession("sad-q", "quick", "Gentle Embrace", 2, listOf("Hands over heart.", "Breathe in: 'I'm here for you.'", "Exhale: 'It's okay to feel this.'", "'This too shall pass.'", "You deserve this kindness.")),
        MoodSession("sad-m", "medium", "Rain & Clearing", 5, listOf("Under gentle rain.", "Drops carry pieces of sadness.", "Don't stop the rain. Witness.", "Drops are cleansing, not hurting.", "Rain begins to lighten.", "Blue sky appears. Warmth.", "Gentler now. That's okay.", "Freshness after the rain.")),
        MoodSession("sad-d", "deep", "Compassion Lake", 10, listOf("Permission to feel.", "Breathe softly. Just breathe.", "Acknowledge what's making you sad.", "Place sadness on a still lake.", "Ripples spread and fade.", "The lake absorbs it completely.", "Someone who loves you sits beside you.", "They don't speak. Simply there.", "Comfort of being witnessed.", "Lake returns to stillness.", "Send yourself love.", "The lake is always there."))
    )),
    Mood("angry", "Angry", "\uD83D\uDE20", Color(210, 150, 150, 230), Color(210, 150, 150, 20), Color(210, 150, 150, 51), "Let's listen without letting it take the wheel.", listOf(
        MoodSession("ang-q", "quick", "Steam Release", 2, listOf("Inhale 4 counts.", "Exhale forcefully, fogging a mirror.", "Five times, releasing heat.", "Shake hands 10 seconds.", "Stop. Fire turned down.")),
        MoodSession("ang-m", "medium", "Flame to Ember", 5, listOf("Anger as a flame in your chest.", "Study it. Colour? Height?", "'I see why you're here.'", "Breathe slowly. Flame flickers lower.", "Becomes ember. Warm, not burning.", "Embers hold power without destruction.", "What do I need right now?", "Thank anger. Let ember rest.")),
        MoodSession("ang-d", "deep", "River of Release", 10, listOf("Breaths to settle racing energy.", "Where does anger live? Jaw? Fists?", "Breathe into that place.", "A powerful river in a canyon.", "Anger is the water — crashing.", "You are the canyon. Containing.", "Water rages. Allowed to be loud.", "Terrain levels. River widens.", "Same water flows gently now.", "Anger transformed to clarity.", "What boundary was crossed?", "Honoured anger can guide."))
    )),
    Mood("overwhelmed", "Overwhelmed", "\uD83C\uDF00", Color(180, 165, 200, 230), Color(180, 165, 200, 20), Color(180, 165, 200, 51), "The bravest thing is to pause.", listOf(
        MoodSession("ovr-q", "quick", "One Thing", 1, listOf("Stop. Not everything, not now.", "One breath. Best of your day.", "One object. Focus only on it.", "Study 30 seconds. Nothing else.", "You can focus. That's enough.")),
        MoodSession("ovr-m", "medium", "Sorting Room", 5, listOf("Three boxes: Now, Later, Never.", "Worries float like papers.", "Catch one. Now, Later, or Never?", "Place it. Satisfaction.", "Next. Trust your instinct.", "Air clears.", "'Now' box is small. Manageable.", "Just what's in 'Now.'")),
        MoodSession("ovr-d", "deep", "Infinite Space", 10, listOf("Lie down. Be supported.", "Breathe naturally. No goals.", "Float in warm gentle darkness.", "No walls, edges, deadlines.", "Every demand dissolves.", "Weightless. Nowhere to be.", "Float. Breathe. Nothingness holds you.", "What matters most rises gently.", "Not a task — a feeling.", "Hold this one thing close.", "Feel body: fingertips, toes.", "You've touched your centre."))
    )),
    Mood("restless", "Restless", "⚡", Color(200, 190, 130, 230), Color(200, 190, 130, 20), Color(200, 190, 130, 51), "Channel it instead of fighting it.", listOf(
        MoodSession("rst-q", "quick", "Energy Reset", 2, listOf("Shake whole body 15 seconds.", "Stop suddenly. Feel buzzing.", "Three deep breaths downward.", "Neck rolls, twice each way.", "Edge softened. Reset.")),
        MoodSession("rst-m", "medium", "Channel & Focus", 5, listOf("Feel restless energy. Where?", "Spinning wheel of light.", "Direct it down your arms.", "Open/close fists rhythmically.", "Direct through legs into earth.", "Draining from chest, shoulders.", "Wheel slows. Balanced.", "Energy is fuel, not friction.")),
        MoodSession("rst-d", "deep", "Still Point", 10, listOf("Hardest part — that's okay.", "Count breaths to 10.", "Restart with kindness.", "Centre of a spinning top.", "World whirls. Centre is still.", "Finding that centre point.", "Breathe into stillness.", "Still point expands.", "Notice fidget urges. Let pass.", "Simply witness impulses.", "Minute in expanded stillness.", "Still point lives inside you."))
    )),
    Mood("lonely", "Lonely", "\uD83C\uDF19", Color(160, 175, 200, 230), Color(160, 175, 200, 20), Color(160, 175, 200, 51), "Your heart is open and seeking.", listOf(
        MoodSession("lon-q", "quick", "Self-Connection", 2, listOf("Hand over heart.", "Someone happy to hear from you.", "'May you be well.'", "'May I be well.'", "Connected through caring.")),
        MoodSession("lon-m", "medium", "Web of Connection", 5, listOf("Golden thread from your heart.", "Reaches someone you love.", "Another to a friend. Family.", "Colleague, neighbour, kind stranger.", "Centre of a beautiful web.", "Threads remain always.", "Tug one. Feel it vibrate.", "Woven into others' lives.")),
        MoodSession("lon-d", "deep", "Campfire Circle", 10, listOf("Welcome some guests.", "Warm campfire in a clearing.", "Feel its warmth.", "People you love arrive.", "Presence fills the space.", "Warmth of being known.", "Past people join too.", "Even those far away.", "All connections live in you.", "Someone smiles across flames.", "Sit with belonging.", "Fire always burning. Always here."))
    )),
    Mood("unfocused", "Unfocused", "\uD83C\uDF2B\uFE0F", Color(170, 185, 170, 230), Color(170, 185, 170, 20), Color(170, 185, 170, 51), "Just needs a gentle breeze to clear.", listOf(
        MoodSession("foc-q", "quick", "Sharp Breath", 1, listOf("Sit straight. Shoulders back.", "5 quick sharp nose breaths.", "Long slow mouth exhale.", "Three times. Fog lifts.", "Eyes wide. Sharper now.")),
        MoodSession("foc-m", "medium", "Laser Focus", 5, listOf("Mind: room of open tabs.", "Close the worry tab.", "Close dinner tab. Social media.", "Until one remains: now.", "Focus on breathing. One tab.", "New tab? Gently close.", "One minute, pure focus.", "Carry clarity forward.")),
        MoodSession("foc-d", "deep", "Fog Clearing", 10, listOf("Five deep breaths.", "Mind: a valley in thick fog.", "Can barely see. That's okay.", "Walk slowly. Trust each step.", "A breeze stirs as you breathe.", "Fog thins. Trees, path emerge.", "Breeze grows warmer.", "Path clear now.", "In the clearing: what matters most.", "Walk toward it. Waiting for you.", "Full visibility. Full presence.", "Fog was never permanent."))
    ))
)

val JOURNAL_PROMPTS = listOf(
    "What came up during that session?",
    "What are you carrying right now?",
    "What would you like to let go of today?",
    "What made you smile recently?",
    "What does peace feel like in your body?",
    "Write a kind sentence to yourself.",
    "What's one small win from today?",
    "What are you grateful for right now?"
)
