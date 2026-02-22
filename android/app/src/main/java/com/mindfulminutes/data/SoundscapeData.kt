package com.mindfulminutes.data

import androidx.compose.ui.graphics.Color

data class Soundscape(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val audioUrl: String
)

data class EscapeJourney(
    val id: String,
    val name: String,
    val mins: Int,
    val icon: String,
    val color: Color,
    val steps: List<String>
)

/**
 * Ambient audio URLs sourced from free/public domain libraries.
 * These are royalty-free ambient loops from Pixabay and similar free audio platforms.
 * All audio is licensed for free use (Pixabay Content License / CC0).
 *
 * To bundle audio locally instead of streaming:
 * 1. Download .mp3 files from the URLs below
 * 2. Place them in app/src/main/res/raw/
 * 3. Update SoundscapeEngine to use R.raw references
 */
val SOUNDSCAPES = listOf(
    Soundscape("rain", "Gentle Rain", "\uD83C\uDF27\uFE0F", Color(140, 170, 200, 230),
        "https://cdn.pixabay.com/audio/2022/10/30/audio_a583b1aa2f.mp3"), // Rain ambient
    Soundscape("forest", "Forest Morning", "\uD83C\uDF32", Color(130, 180, 130, 230),
        "https://cdn.pixabay.com/audio/2022/08/31/audio_419263fc12.mp3"), // Forest birds
    Soundscape("ocean", "Ocean Waves", "\uD83C\uDF0A", Color(130, 175, 210, 230),
        "https://cdn.pixabay.com/audio/2022/05/31/audio_980b47e680.mp3"), // Ocean waves
    Soundscape("fire", "Crackling Fire", "\uD83D\uDD25", Color(210, 160, 120, 230),
        "https://cdn.pixabay.com/audio/2024/11/14/audio_3760e4eb1a.mp3"), // Fireplace
    Soundscape("wind", "Mountain Wind", "\uD83C\uDFD4\uFE0F", Color(180, 190, 200, 230),
        "https://cdn.pixabay.com/audio/2023/10/26/audio_86949b0445.mp3"), // Wind ambient
    Soundscape("birds", "Birdsong", "\uD83D\uDC26", Color(200, 190, 140, 230),
        "https://cdn.pixabay.com/audio/2022/03/09/audio_c8ab0dbe84.mp3"), // Birds singing
    Soundscape("bowls", "Singing Bowls", "\uD83D\uDD14", Color(190, 170, 200, 230),
        "https://cdn.pixabay.com/audio/2023/07/17/audio_4b21202e65.mp3"), // Singing bowls
    Soundscape("stream", "Babbling Stream", "\uD83D\uDCA7", Color(150, 195, 200, 230),
        "https://cdn.pixabay.com/audio/2024/06/11/audio_c0a98c29eb.mp3")  // Stream water
)

val ESCAPE_JOURNEYS = listOf(
    EscapeJourney("ej1", "Autumn Forest Walk", 10, "\uD83C\uDF42", Color(200, 160, 100, 230), listOf(
        "You stand at the edge of an ancient forest in autumn.",
        "Golden and amber leaves carpet the ground beneath your feet.",
        "Step onto the soft path. Hear leaves crunch gently with each step.",
        "Sunlight filters through the canopy in warm, honey-coloured beams.",
        "A gentle breeze carries the scent of earth and wood smoke.",
        "You pass a mossy boulder. Trail your fingers across its cool surface.",
        "The path opens to a clearing with a still pond reflecting the trees.",
        "Sit beside the water. Watch a single leaf spiral down to the surface.",
        "Ripples spread outward, then stillness returns.",
        "Breathe in the peace of this timeless place.",
        "When ready, rise and walk back, carrying the forest's calm.",
        "The path behind you glows golden. You are renewed."
    )),
    EscapeJourney("ej2", "Moonlit Beach", 8, "\uD83C\uDF15", Color(160, 175, 210, 230), listOf(
        "You arrive at a deserted beach under a full moon.",
        "Silver light paints everything in soft blue and white.",
        "Sand is cool and smooth beneath your bare feet.",
        "Waves arrive in slow, rhythmic pulses.",
        "Walk along the waterline. Foam tickles your ankles.",
        "Find a smooth piece of driftwood and sit.",
        "The moon's reflection stretches across the water toward you.",
        "Each wave brings calm. Each retreat takes worry.",
        "Stars multiply above — countless points of light.",
        "You are small and held by something vast and gentle.",
        "Let the sound of the ocean fill every corner of your mind.",
        "Rise when ready. The beach will wait for you."
    )),
    EscapeJourney("ej3", "Mountain Sunrise", 10, "\uD83C\uDFD4\uFE0F", Color(210, 170, 140, 230), listOf(
        "You sit on a mountain summit before dawn.",
        "The air is crisp and clean. Stars still visible overhead.",
        "A thin line of gold appears on the eastern horizon.",
        "It spreads — peach, then rose, then blazing amber.",
        "The first ray of sun touches your face. Feel its warmth.",
        "Valleys below fill with golden mist.",
        "Mountains emerge like islands in a sea of light.",
        "Birds begin their morning songs far below.",
        "The whole world wakes up around you.",
        "You were here to witness the very first moment.",
        "Gratitude fills your chest like the spreading light.",
        "Carry this dawn within you throughout your day."
    ))
)
