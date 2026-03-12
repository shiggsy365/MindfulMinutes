package com.mindfulminutes.data

import androidx.compose.ui.graphics.Color

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val bg: Color,
    val border: Color,
    val desc: String
)

data class Exercise(
    val id: String,
    val cat: String,
    val name: String,
    val mins: Int,
    val steps: List<String>,
    val cycles: Int? = null,
    val showAllSteps: Boolean = false
)

data class BreathingPattern(
    val inhale: Int,
    val hold1: Int,
    val exhale: Int,
    val hold2: Int,
    val label: String,
    val instructions: Map<String, String>
)

data class BreathPhase(
    val phase: String,
    val countdown: Int,
    val progress: Float
)

val CATEGORIES = listOf(
    Category("breathing", "Breathing", "\uD83C\uDF2C\uFE0F", Color(130, 190, 200, 230), Color(130, 190, 200, 20), Color(130, 190, 200, 51), "Anchor yourself through the rhythm of your breath"),
    Category("body", "Body Scan", "\uD83E\uDDD8", Color(180, 160, 210, 230), Color(180, 160, 210, 20), Color(180, 160, 210, 51), "Tune into the sensations held within your body"),
    Category("senses", "Senses", "\uD83D\uDC41\uFE0F", Color(200, 180, 140, 230), Color(200, 180, 140, 20), Color(200, 180, 140, 51), "Awaken awareness through sight, sound, and touch"),
    Category("gratitude", "Gratitude", "\uD83C\uDF3F", Color(150, 195, 150, 230), Color(150, 195, 150, 20), Color(150, 195, 150, 51), "Cultivate appreciation for the present moment"),
    Category("movement", "Mindful Movement", "\uD83C\uDF0A", Color(140, 175, 210, 230), Color(140, 175, 210, 20), Color(140, 175, 210, 51), "Find stillness through gentle, intentional motion"),
    Category("visualisation", "Visualisation", "✨", Color(210, 170, 180, 230), Color(210, 170, 180, 20), Color(210, 170, 180, 51), "Journey inward through the landscape of imagination")
)

val EXERCISES = listOf(
    // Breathing
    Exercise("b1", "breathing", "Box Breathing", 3, listOf("Breathe in slowly through your nose for 4 counts.", "Hold your breath gently for 4 counts.", "Exhale slowly through your mouth for 4 counts.", "Hold empty for 4 counts. Repeat the cycle.", "With each round, feel your heartbeat slow and your mind settle."), cycles = 4),
    Exercise("b2", "breathing", "4-7-8 Calm", 2, listOf("Place the tip of your tongue behind your upper front teeth.", "Inhale quietly through your nose for 4 counts.", "Hold your breath for 7 counts.", "Exhale completely through your mouth for 8 counts, making a whoosh sound.", "Repeat three more times, letting each exhale release more tension."), cycles = 4),
    Exercise("b3", "breathing", "Ocean Breath", 5, listOf("Slightly constrict the back of your throat.", "Inhale deeply through your nose, creating a soft ocean-like sound.", "Exhale slowly through your nose with the same gentle constriction.", "Let the rhythm of your breath become the rhythm of waves.", "Continue for several minutes, riding each wave of breath.")),
    Exercise("b4", "breathing", "Belly Breathing", 3, listOf("Place one hand on your chest and one on your belly.", "Breathe in deeply so only your belly hand rises.", "Exhale and feel your belly gently fall.", "Keep your chest hand as still as possible.", "Continue, feeling the gentle rise and fall like a sleeping child.")),
    Exercise("b5", "breathing", "Alternate Nostril", 4, listOf("Close your right nostril with your thumb and inhale through the left.", "Close the left nostril with your ring finger, release the right.", "Exhale through the right nostril slowly.", "Inhale through the right, then switch and exhale through the left.", "Continue alternating, feeling balance return to your mind."), cycles = 6),
    Exercise("b6", "breathing", "Counted Exhale", 3, listOf("Inhale naturally without counting.", "Exhale slowly, counting each second: one... two... three...", "Try to extend your exhale a little longer each time.", "Aim for an exhale that's twice as long as your inhale.", "Feel your nervous system shift toward calm with each breath."), showAllSteps = true),
    Exercise("b7", "breathing", "Straw Breathing", 3, listOf("Inhale deeply through your nose.", "Purse your lips as if breathing through a tiny straw.", "Exhale as slowly as possible through your pursed lips.", "Feel the controlled release of air grounding you.", "Repeat five times, each exhale longer than the last."), showAllSteps = true),
    Exercise("b8", "breathing", "Energising Breath", 3, listOf("Sit tall and take three normal breaths to settle.", "Begin quick, rhythmic breaths through your nose — equal inhale and exhale.", "Keep breaths short and pumping from the diaphragm for 15 seconds.", "Stop and take one long, deep breath in. Hold for 5 counts.", "Exhale slowly. Notice the tingling aliveness in your body."), showAllSteps = true),
    Exercise("b9", "breathing", "Sighing Release", 3, listOf("Inhale deeply through your nose, filling your lungs completely.", "Let out an audible sigh — a big, dramatic exhale through your mouth.", "Feel the release of tension with the sound.", "Inhale again deeply. Sigh it out even louder.", "Three more sighs, each one letting go of something you're carrying."), showAllSteps = true),
    Exercise("b10", "breathing", "Candle Breathing", 3, listOf("Imagine a candle flame at arm's length in front of you.", "Inhale deeply through your nose.", "Exhale so gently that you would only make the flame flicker, not go out.", "Focus entirely on the smoothness and control of your exhale.", "Continue for ten breaths, each softer than the last."), showAllSteps = true),

    // Body Scan
    Exercise("bs1", "body", "Head to Toe Scan", 5, listOf("Close your eyes and bring attention to the crown of your head.", "Slowly move your awareness down: forehead, eyes, jaw — releasing tension.", "Continue through your neck, shoulders, arms, and fingertips.", "Scan through your chest, belly, hips, and lower back.", "Flow down through your legs to the tips of your toes. Breathe.")),
    Exercise("bs2", "body", "Tension Spotter", 3, listOf("Take three slow breaths to settle into stillness.", "Scan your body and find the place holding the most tension.", "Breathe directly into that area — imagine warmth flowing in.", "With each exhale, picture the tension dissolving like mist.", "When it softens, scan for the next spot. Repeat until at ease.")),
    Exercise("bs3", "body", "Warm Light Scan", 4, listOf("Imagine a warm, golden light resting above your head.", "As you inhale, draw this light down into your scalp and face.", "With each breath, let it flow further — neck, chest, arms.", "Feel its warmth relaxing every muscle it touches.", "Let it fill your entire body until you glow from within.")),
    Exercise("bs4", "body", "Hands Awareness", 2, listOf("Rest your hands palms-up on your knees.", "Bring all attention to your left hand. Feel its weight.", "Notice temperature, tingling, the pulse in your fingertips.", "Shift attention to your right hand. Observe without judging.", "Hold awareness of both hands at once. Feel them alive.")),
    Exercise("bs5", "body", "Grounding Feet", 2, listOf("Press your feet flat on the ground.", "Feel the floor — its temperature, texture, firmness.", "Imagine roots from your soles deep into the earth.", "With each breath, feel more anchored.", "Wiggle your toes. You are here. You are grounded.")),
    Exercise("bs6", "body", "Jaw & Face Release", 2, listOf("Unclench your jaw. Let your mouth fall slightly open.", "Soften the muscles around your eyes.", "Relax your forehead, smoothing away furrows.", "Let your tongue rest softly at the bottom of your mouth.", "Notice how much tension your face was holding.")),
    Exercise("bs7", "body", "Progressive Relaxation", 5, listOf("Curl your toes tightly 5 seconds, then release.", "Tense calves and thighs 5 seconds. Let go.", "Squeeze fists, tighten arms. Hold. Release.", "Scrunch shoulders to ears. Hold. Drop them.", "Tense your whole face. Hold. Release. Deeply relaxed.")),
    Exercise("bs8", "body", "Heartbeat Meditation", 3, listOf("Place your hand over your heart.", "Feel the steady rhythm beneath your palm.", "With each beat, silently say 'here.'", "If your mind wanders, return to the beat.", "Feel gratitude for this tireless rhythm.")),
    Exercise("bs9", "body", "Spine Check-In", 2, listOf("Notice your posture without correcting it.", "From tailbone, scan upward along your spine.", "Notice curves, tension, or discomfort.", "Imagine a thread pulling up from the crown.", "Feel the quiet dignity of an upright spine.")),
    Exercise("bs10", "body", "Breath in the Body", 3, listOf("Breathe normally. Where do you feel it most?", "Nostrils? Chest? Belly?", "Follow the air inward — feel lungs expand.", "Follow it out — the gentle release.", "Witness breath moving through you like a visitor.")),

    // Senses
    Exercise("s1", "senses", "5-4-3-2-1 Grounding", 3, listOf("Name 5 things you can see. Really look.", "Name 4 things you can touch. Feel textures.", "Name 3 things you can hear. Even quiet ones.", "Name 2 things you can smell. Breathe deeply.", "Name 1 thing you can taste. Let it linger.")),
    Exercise("s2", "senses", "Deep Listening", 3, listOf("Close your eyes. Become still.", "Listen for the farthest sound.", "Find the closest, most subtle sound.", "Hold awareness of distant and near.", "Let sounds wash over you. Just listen.")),
    Exercise("s3", "senses", "Texture Explorer", 2, listOf("Find a nearby object.", "Close eyes. Explore with fingertips.", "Notice ridges, temperature, surface quality.", "Move slowly. As if for the first time.", "Open eyes. See it with fresh appreciation.")),
    Exercise("s4", "senses", "Colour Seeking", 2, listOf("Choose a colour that calls to you.", "Find every instance around you.", "Notice shades you'd normally overlook.", "How does this change your perception?", "Appreciate the richness surrounding you.")),
    Exercise("s5", "senses", "Mindful Sip", 2, listOf("Take a drink nearby.", "Feel the weight and temperature of the cup.", "Bring it to your lips slowly.", "One small sip. Hold it. Notice every flavour.", "Swallow slowly. One sip, fully lived.")),
    Exercise("s6", "senses", "Skygazing", 3, listOf("Look up at the sky.", "Notice colours: blues, greys, whites, golds.", "Watch clouds drift without destination.", "Let thoughts be like clouds — passing through.", "Feel the vast openness. Small and peaceful.")),
    Exercise("s7", "senses", "Sound Bath", 4, listOf("Sit with ambient sounds.", "Let all sounds arrive without filtering.", "Isolate one sound. Follow its rhythm.", "Release it. Pick another.", "Let all sounds merge into a symphony.")),
    Exercise("s8", "senses", "Barefoot Moment", 2, listOf("Remove shoes if you can.", "Place bare feet on the ground.", "Notice temperature, texture, pressure.", "Shift weight slowly from heel to toe.", "The earth supports you completely.")),
    Exercise("s9", "senses", "Scent Journey", 2, listOf("Find something with a scent.", "Hold near your nose. Inhale slowly.", "Notice layers. Does it change?", "Let it trigger memories. Just notice.", "Appreciate this invisible, powerful sense.")),
    Exercise("s10", "senses", "Peripheral Vision", 1, listOf("Fix gaze on a single point ahead.", "Expand awareness to the edges.", "Notice shapes, colours, movement.", "Hold this wide, soft focus 30 seconds.", "Panoramic awareness shifts you out of stress.")),

    // Gratitude
    Exercise("g1", "gratitude", "Three Good Things", 2, listOf("Three good things from today.", "They can be tiny — warmth, kindness, sunlight.", "Sit with each feeling for a few breaths.", "Whisper 'thank you' for each one.", "Carry this warmth forward.")),
    Exercise("g2", "gratitude", "Body Gratitude", 3, listOf("Thank your hands for everything they do.", "Thank your legs for carrying you.", "Thank your eyes for beauty they show.", "Thank your lungs for every breath.", "Your body works tirelessly. Appreciate it.")),
    Exercise("g3", "gratitude", "Person Appreciation", 3, listOf("Someone who positively impacted your life.", "Picture their face. A specific moment.", "Feel gratitude as warmth in your chest.", "Send them: 'May you be happy.'", "Consider telling them how you feel.")),
    Exercise("g4", "gratitude", "Ordinary Miracles", 2, listOf("Find something utterly ordinary.", "Consider its journey to be here.", "People, materials, effort involved.", "Let wonder fill you at the invisible web.", "The ordinary is extraordinary.")),
    Exercise("g5", "gratitude", "Gratitude Letter", 5, listOf("Someone you've never properly thanked.", "Compose a short letter mentally.", "What they did. How it affected you.", "Feel emotion with each mental line.", "The feeling is enough for now.")),
    Exercise("g6", "gratitude", "Senses Gratitude", 2, listOf("Thank eyes for one beautiful thing.", "Thank ears for one comforting sound.", "Thank skin for one soothing touch.", "Thank nose for one alive scent.", "Thank tongue for one joyful taste.")),
    Exercise("g7", "gratitude", "Past Self Thanks", 3, listOf("A difficult time you survived.", "Thank your past self for enduring.", "Acknowledge the strength it took.", "Recognise how it shaped you.", "You're here because you kept going.")),
    Exercise("g8", "gratitude", "Comfort Inventory", 2, listOf("Notice comfort you're experiencing now.", "Roof. Clothing. Air in your lungs.", "Not guaranteed for everyone.", "Feel genuine appreciation.", "Consider sharing comfort with others.")),
    Exercise("g9", "gratitude", "Future Gratitude", 2, listOf("Imagine yourself one year from now, at peace.", "They look back at today with gratitude.", "What seeds are you planting now?", "Feel the connection between today and tomorrow.", "Trust that what you're building matters.")),
    Exercise("g10", "gratitude", "Meal Blessing", 1, listOf("Pause before eating. Look at the food.", "Think of sun, rain, soil that grew it.", "Think of hands that prepared it.", "Silent gratitude for nourishment.", "First bite slowly, tasting gratitude.")),

    // Mindful Movement
    Exercise("m1", "movement", "Gentle Neck Rolls", 2, listOf("Drop chin to chest. Feel the stretch.", "Roll head to the right slowly.", "Continue back, then left. Full circle.", "Incredibly slowly — every micro-sensation.", "Reverse. Three circles each way.")),
    Exercise("m2", "movement", "Standing Mountain", 3, listOf("Feet hip-width. Feel the ground.", "Press all four corners of each foot.", "Stack hips, shoulders, head upward.", "Crown reaches toward sky. Arms at sides.", "Still, strong, unmoved by thoughts.")),
    Exercise("m3", "movement", "Shoulder Waterfall", 2, listOf("Inhale, shoulders up to ears.", "Hold high and tight 3 seconds.", "Exhale, let them drop — waterfall.", "Feel tension versus release.", "Five times. Each drop washes stress.")),
    Exercise("m4", "movement", "Mindful Walking", 5, listOf("Stand still. Weight on your feet.", "Begin walking very slowly.", "Notice weight shift, leg swing, placement.", "Each step deliberate. Nowhere else to be.", "Heel, ball, toe. Heel, ball, toe.")),
    Exercise("m5", "movement", "Cat-Cow Stretch", 2, listOf("Hands and knees position.", "Inhale: belly drops, chin lifts.", "Exhale: round spine, tuck chin.", "Flow between shapes with breath.", "Spine liquid, moving like a wave.")),
    Exercise("m6", "movement", "Finger Tap Rhythm", 1, listOf("Hands on a flat surface.", "Tap each finger to thumb in sequence.", "Forward and backward, full attention.", "Speed up, then slow down.", "Simple rhythm, out of head, into hands.")),
    Exercise("m7", "movement", "Ragdoll Fold", 2, listOf("Stand hip-width. Deep breath in.", "Fold forward, arms and head hang.", "Grab opposite elbows. Sway gently.", "Let gravity do the work.", "Slowly roll up, one vertebra at a time.")),
    Exercise("m8", "movement", "Wrist & Hand Release", 2, listOf("Extend arms. Make fists. Squeeze 3 sec.", "Release. Spread fingers wide.", "Rotate wrists — five circles each way.", "Shake hands loosely, flicking water.", "Palms together. Breathe. Be grateful.")),
    Exercise("m9", "movement", "Slow Stretch Reach", 2, listOf("Inhale, arms overhead.", "Reach high. Whole body lengthens.", "Lean right. Two breaths.", "Lean left. Two breaths.", "Lower slowly. Feel the space you created.")),
    Exercise("m10", "movement", "Seated Twist", 2, listOf("Sit tall, feet flat.", "Right hand on left knee.", "Inhale lengthen, exhale twist left.", "Hold three breaths, look over shoulder.", "Return to centre. Repeat other side.")),

    // Visualisation
    Exercise("v1", "visualisation", "Safe Place", 4, listOf("Imagine where you feel completely safe.", "Build it: colours, light, details.", "What sounds? What temperature?", "Place yourself in the centre. Breathe.", "This place is always within you.")),
    Exercise("v2", "visualisation", "Floating Leaf", 3, listOf("A slow, clear stream in a peaceful forest.", "Each thought — place it on a leaf.", "Watch it float downstream and away.", "Don't chase it. Another will come.", "The stream always flows.")),
    Exercise("v3", "visualisation", "Inner Garden", 5, listOf("Step into a beautiful garden.", "Your garden — designed by your deepest peace.", "Walk the paths. What blooms? What stands tall?", "Find a bench. Sit. Birds. Warm light.", "Plant a seed of intention before you leave.")),
    Exercise("v4", "visualisation", "Colour Breathing", 2, listOf("Choose a colour that means calm.", "Inhale it as a soft light.", "Feel it fill lungs, spread through body.", "Exhale murky grey — your stress.", "Inhale colour. Exhale grey. Glow with calm.")),
    Exercise("v5", "visualisation", "Mountain Meditation", 4, listOf("A great mountain — solid, majestic.", "Seasons pass: snow, rain, sun, wind.", "The mountain remains. Still. Complete.", "You are this mountain. Emotions are weather.", "Sit with inner strength and permanence.")),
    Exercise("v6", "visualisation", "Ocean of Calm", 3, listOf("Standing at a vast, calm ocean shore.", "Warm, gentle. Waves lap softly.", "Wade in slowly. Feel water support you.", "Float weightless. Infinite blue sky.", "Each wave rocks you deeper into peace.")),
    Exercise("v7", "visualisation", "Starlight Shower", 3, listOf("Lying on soft grass under night sky.", "Stars fall softly like glowing snow.", "Each one dissolves a worry on landing.", "Forehead, chest, arms, legs.", "Covered in starlight. Light as air. Free.")),
    Exercise("v8", "visualisation", "Letting Go Balloon", 2, listOf("Something weighing on your mind.", "Write it on a tag. Tie to a balloon.", "Choose the colour. Hold the string.", "Deep breath. Release the string.", "Watch it rise and disappear. Breathe.")),
    Exercise("v9", "visualisation", "Warm Cocoon", 3, listOf("A soft, warm cocoon around your body.", "Perfect weight — a hug from all directions.", "The world is muffled. Only warmth.", "Muscles surrender. Mind quiets.", "You are held. You are safe.")),
    Exercise("v10", "visualisation", "Sunrise Within", 3, listOf("Golden light in the centre of your chest.", "With each breath, brighter and warmer.", "Expanding outward — filling your whole body.", "Darkness transforms to golden warmth.", "You carry your own light. You always have."))
)

fun getBreathingPattern(exercise: Exercise): BreathingPattern {
    val n = exercise.name.lowercase()
    return when {
        "box" in n -> BreathingPattern(4, 4, 4, 4, "Box", mapOf("inhale" to "Breathe in slowly through your nose\u2026", "hold1" to "Hold gently\u2026", "exhale" to "Exhale slowly through your mouth\u2026", "hold2" to "Hold empty \u2014 feel the stillness\u2026"))
        "4-7-8" in n -> BreathingPattern(4, 7, 8, 0, "4-7-8", mapOf("inhale" to "Inhale quietly through your nose\u2026", "hold1" to "Hold \u2014 stay relaxed\u2026", "exhale" to "Exhale completely with a whoosh\u2026", "hold2" to ""))
        "ocean" in n -> BreathingPattern(5, 0, 5, 0, "Ocean", mapOf("inhale" to "Inhale deeply, soft ocean sound\u2026", "hold1" to "", "exhale" to "Exhale slowly, gentle constriction\u2026", "hold2" to ""))
        "belly" in n -> BreathingPattern(5, 0, 5, 0, "Belly", mapOf("inhale" to "Breathe in \u2014 feel your belly rise\u2026", "hold1" to "", "exhale" to "Exhale \u2014 belly gently falls\u2026", "hold2" to ""))
        "alternate" in n -> BreathingPattern(4, 2, 4, 2, "Alternate", mapOf("inhale" to "Inhale through one nostril\u2026", "hold1" to "Pause \u2014 switch\u2026", "exhale" to "Exhale through the other\u2026", "hold2" to "Pause before switching\u2026"))
        "counted" in n -> BreathingPattern(3, 0, 6, 0, "Counted", mapOf("inhale" to "Inhale naturally\u2026", "hold1" to "", "exhale" to "Exhale slowly, counting each second\u2026", "hold2" to ""))
        "straw" in n -> BreathingPattern(3, 0, 7, 0, "Straw", mapOf("inhale" to "Inhale deeply through your nose\u2026", "hold1" to "", "exhale" to "Exhale through pursed lips\u2026", "hold2" to ""))
        "energi" in n -> BreathingPattern(1, 0, 1, 0, "Rhythmic", mapOf("inhale" to "Quick breath in\u2026", "hold1" to "", "exhale" to "Quick breath out\u2026", "hold2" to ""))
        "sigh" in n -> BreathingPattern(4, 0, 6, 0, "Sighing", mapOf("inhale" to "Inhale deeply, filling lungs\u2026", "hold1" to "", "exhale" to "Let out an audible sigh\u2026", "hold2" to ""))
        "candle" in n -> BreathingPattern(4, 0, 7, 0, "Candle", mapOf("inhale" to "Inhale deeply\u2026", "hold1" to "", "exhale" to "Exhale gently \u2014 flame just flickers\u2026", "hold2" to ""))
        else -> BreathingPattern(4, 2, 6, 0, "Calm", mapOf("inhale" to "Breathe in slowly\u2026", "hold1" to "Hold gently\u2026", "exhale" to "Exhale slowly\u2026", "hold2" to ""))
    }
}

fun getBreathPhase(pattern: BreathingPattern, elapsed: Int): BreathPhase {
    val cycle = pattern.inhale + pattern.hold1 + pattern.exhale + pattern.hold2
    val pos = elapsed % cycle
    return when {
        pos < pattern.inhale -> BreathPhase("inhale", kotlin.math.ceil((pattern.inhale - pos).toDouble()).toInt(), pos.toFloat() / pattern.inhale)
        pattern.hold1 > 0 && pos < pattern.inhale + pattern.hold1 -> BreathPhase("hold1", kotlin.math.ceil((pattern.hold1 - (pos - pattern.inhale)).toDouble()).toInt(), (pos - pattern.inhale).toFloat() / pattern.hold1)
        pos < pattern.inhale + pattern.hold1 + pattern.exhale -> BreathPhase("exhale", kotlin.math.ceil((pattern.exhale - (pos - pattern.inhale - pattern.hold1)).toDouble()).toInt(), (pos - pattern.inhale - pattern.hold1).toFloat() / pattern.exhale)
        else -> BreathPhase("hold2", kotlin.math.ceil((pattern.hold2 - (pos - pattern.inhale - pattern.hold1 - pattern.exhale)).toDouble()).toInt(), (pos - pattern.inhale - pattern.hold1 - pattern.exhale).toFloat() / pattern.hold2)
    }
}

fun getCategoryForExercise(exercise: Exercise): Category {
    return CATEGORIES.find { it.id == exercise.cat } ?: CATEGORIES[0]
}
