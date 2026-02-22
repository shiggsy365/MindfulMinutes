package com.mindfulminutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.audio.SoundscapeEngine
import com.mindfulminutes.data.*
import com.mindfulminutes.ui.components.PillButton
import com.mindfulminutes.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun GuidedEscapesScreen() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf("sounds") }

    // Soundscape state
    val engine = remember { SoundscapeEngine(context) }
    var activeVolumes by remember { mutableStateOf(mapOf<String, Float>()) }
    var sleepTimer by remember { mutableStateOf<Int?>(null) }
    var sleepLeft by remember { mutableIntStateOf(0) }

    // Journey state
    var activeJourney by remember { mutableStateOf<EscapeJourney?>(null) }
    var journeyPhase by remember { mutableStateOf("preview") }
    var journeyTime by remember { mutableIntStateOf(0) }
    var journeyStep by remember { mutableIntStateOf(0) }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose { engine.release() }
    }

    // Journey timer
    LaunchedEffect(journeyPhase, activeJourney) {
        if (journeyPhase == "active" && activeJourney != null) {
            val total = activeJourney!!.mins * 60
            journeyTime = 0
            while (journeyTime < total) {
                delay(1000)
                journeyTime++
                val stepDuration = total.toFloat() / activeJourney!!.steps.size
                journeyStep = min(
                    (journeyTime / stepDuration).toInt(),
                    activeJourney!!.steps.size - 1
                )
                if (journeyTime >= total) {
                    journeyPhase = "complete"
                }
            }
        }
    }

    fun toggleSound(id: String) {
        if (activeVolumes.containsKey(id)) {
            engine.stop(id)
            activeVolumes = activeVolumes - id
        } else {
            val soundscape = SOUNDSCAPES.find { it.id == id } ?: return
            engine.play(id, soundscape.audioUrl, 0.7f)
            activeVolumes = activeVolumes + (id to 0.7f)
        }
    }

    fun setVolume(id: String, vol: Float) {
        engine.setVolume(id, vol)
        activeVolumes = activeVolumes + (id to vol)
    }

    fun setSleepTimerMinutes(minutes: Int?) {
        if (minutes == null) {
            engine.cancelSleepTimer()
            sleepTimer = null
            sleepLeft = 0
        } else {
            sleepTimer = minutes
            sleepLeft = minutes * 60
            engine.startSleepTimer(
                minutes,
                onTick = { remaining -> sleepLeft = remaining },
                onComplete = {
                    activeVolumes = emptyMap()
                    sleepTimer = null
                    sleepLeft = 0
                }
            )
        }
    }

    // Journey active overlay
    if (activeJourney != null && journeyPhase != "preview") {
        JourneyOverlay(
            journey = activeJourney!!,
            phase = journeyPhase,
            time = journeyTime,
            step = journeyStep,
            onClose = {
                activeJourney = null
                journeyPhase = "preview"
                journeyTime = 0
                journeyStep = 0
            },
            onBegin = {
                journeyTime = 0
                journeyStep = 0
                journeyPhase = "active"
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0E1214), Background, Color(0xFF101418)),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        // Header
        Text(
            text = "Guided Escapes",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Mix ambient sounds or follow a guided journey.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Tab bar
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            EscapeTabChip("\uD83C\uDFA7 Soundscapes", tab == "sounds") { tab = "sounds" }
            Spacer(Modifier.width(8.dp))
            EscapeTabChip("\uD83C\uDF3F Journeys", tab == "journeys") { tab = "journeys" }
        }

        Spacer(Modifier.height(20.dp))

        when (tab) {
            "sounds" -> SoundscapesTab(
                activeVolumes = activeVolumes,
                sleepTimer = sleepTimer,
                sleepLeft = sleepLeft,
                onToggleSound = ::toggleSound,
                onSetVolume = ::setVolume,
                onSetSleepTimer = ::setSleepTimerMinutes
            )
            "journeys" -> JourneysTab(
                activeJourney = activeJourney,
                journeyPhase = journeyPhase,
                onSelectJourney = { journey ->
                    activeJourney = journey
                    journeyPhase = "preview"
                    journeyTime = 0
                    journeyStep = 0
                },
                onDeselectJourney = { activeJourney = null },
                onReady = { journeyPhase = "ready" }
            )
        }
    }
}

@Composable
private fun EscapeTabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) CardBgHover else Color.Transparent,
                RoundedCornerShape(100.dp)
            )
            .border(
                1.dp,
                if (selected) Color.White.copy(alpha = 0.15f) else CardBorder,
                RoundedCornerShape(100.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = if (selected) TextPrimary else TextTertiary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun SoundscapesTab(
    activeVolumes: Map<String, Float>,
    sleepTimer: Int?,
    sleepLeft: Int,
    onToggleSound: (String) -> Unit,
    onSetVolume: (String, Float) -> Unit,
    onSetSleepTimer: (Int?) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Sound grid
        item {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.height(280.dp)
            ) {
                items(SOUNDSCAPES) { soundscape ->
                    val isOn = activeVolumes.containsKey(soundscape.id)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isOn) CardBgHover else Color.White.copy(alpha = 0.02f),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isOn) Color.White.copy(alpha = 0.12f) else CardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onToggleSound(soundscape.id) }
                            .padding(vertical = 20.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = soundscape.icon,
                                fontSize = 28.sp,
                                color = if (isOn) Color.Unspecified else Color.Unspecified.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = soundscape.name,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = if (isOn) soundscape.color else TextTertiary
                            )
                        }
                    }
                }
            }
        }

        // Volume sliders for active sounds
        if (activeVolumes.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "NOW PLAYING · ${activeVolumes.size} SOUND${if (activeVolumes.size > 1) "S" else ""}",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = TextMuted,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(16.dp))

                        activeVolumes.forEach { (id, vol) ->
                            val soundscape = SOUNDSCAPES.find { it.id == id } ?: return@forEach
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(text = soundscape.icon, fontSize = 18.sp, modifier = Modifier.width(28.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = soundscape.name,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = soundscape.color,
                                    modifier = Modifier.width(90.dp)
                                )
                                Slider(
                                    value = vol,
                                    onValueChange = { onSetVolume(id, it) },
                                    modifier = Modifier.weight(1f).height(24.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White.copy(alpha = 0.6f),
                                        activeTrackColor = soundscape.color.copy(alpha = 0.5f),
                                        inactiveTrackColor = Color.White.copy(alpha = 0.08f)
                                    )
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${(vol * 100).toInt()}%",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    modifier = Modifier.width(32.dp)
                                )
                            }
                        }

                        // Sleep timer section
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(DividerColor)
                        )
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "SLEEP TIMER",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = TextMuted,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "15m" to 15,
                                "30m" to 30,
                                "60m" to 60,
                                "Off" to null
                            ).forEach { (label, value) ->
                                val selected = sleepTimer == value
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (selected) AccentBg else CardBg,
                                            RoundedCornerShape(100.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) AccentBorder else CardBorder,
                                            RoundedCornerShape(100.dp)
                                        )
                                        .clickable { onSetSleepTimer(value) }
                                        .padding(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 11.sp,
                                        color = if (selected) Accent else TextTertiary
                                    )
                                }
                            }

                            if (sleepLeft > 0) {
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${sleepLeft / 60}:${(sleepLeft % 60).toString().padStart(2, '0')} left",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = Accent.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (activeVolumes.isEmpty()) {
            item {
                Text(
                    text = "Tap sounds to create your mix",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun JourneysTab(
    activeJourney: EscapeJourney?,
    journeyPhase: String,
    onSelectJourney: (EscapeJourney) -> Unit,
    onDeselectJourney: () -> Unit,
    onReady: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(ESCAPE_JOURNEYS) { journey ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                if (activeJourney?.id == journey.id && journeyPhase == "preview") {
                    // Expanded preview
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = journey.icon, fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = journey.name,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${journey.mins} min · ${journey.steps.size} moments",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        journey.steps.forEachIndexed { i, stepText ->
                            Row(
                                modifier = Modifier.padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(CardBg, CircleShape)
                                        .border(1.dp, CardBorder, CircleShape)
                                ) {
                                    Text(
                                        text = "${i + 1}",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = stepText,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            PillButton(
                                text = "BACK",
                                color = TextTertiary,
                                bgColor = CardBg,
                                borderColor = CardBorder,
                                onClick = { onDeselectJourney() }
                            )
                            PillButton(
                                text = "I'M READY",
                                color = Accent,
                                bgColor = AccentBg,
                                borderColor = AccentBorder,
                                onClick = onReady
                            )
                        }
                    }
                } else {
                    // Collapsed card
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectJourney(journey) }
                    ) {
                        Text(text = journey.icon, fontSize = 32.sp)
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = journey.name,
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${journey.mins} min journey",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = journey.steps.first(),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = TextTertiary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JourneyOverlay(
    journey: EscapeJourney,
    phase: String,
    time: Int,
    step: Int,
    onClose: () -> Unit,
    onBegin: () -> Unit
) {
    val progress = if (phase == "complete") 1f else time.toFloat() / (journey.mins * 60)
    val timeLeft = journey.mins * 60 - time
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF7080C0A))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "✕",
                color = TextMuted,
                fontSize = 22.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onClose() }
            )

            Spacer(Modifier.weight(1f))

            Text(text = journey.icon, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))

            Text(
                text = journey.name,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary
            )

            Spacer(Modifier.height(24.dp))

            // Circular progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .drawBehind {
                        val strokeWidth = 3.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)

                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = journey.color.copy(alpha = 0.7f),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
            ) {
                if (phase == "complete") {
                    Text(
                        text = "peace",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        color = journey.color
                    )
                } else {
                    Text(
                        text = "$minutes:${seconds.toString().padStart(2, '0')}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Current step text
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(80.dp).widthIn(max = 360.dp)
            ) {
                Text(
                    text = if (phase == "complete") "You have returned, carrying peace within you." else journey.steps[step],
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Step dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                journey.steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (i <= step && phase == "active") journey.color else Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                    )
                }
            }

            // Action buttons
            when (phase) {
                "ready" -> PillButton(
                    text = "BEGIN JOURNEY",
                    color = Accent,
                    bgColor = AccentBg,
                    borderColor = AccentBorder,
                    onClick = onBegin
                )
                "complete" -> PillButton(
                    text = "RETURN",
                    color = Accent,
                    bgColor = AccentBg,
                    borderColor = AccentBorder,
                    onClick = onClose
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
