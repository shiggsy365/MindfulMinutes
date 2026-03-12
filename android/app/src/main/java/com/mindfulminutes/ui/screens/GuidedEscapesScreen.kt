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
import androidx.compose.ui.draw.alpha
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
import com.mindfulminutes.LocalSpeechManager
import com.mindfulminutes.data.*
import com.mindfulminutes.ui.components.PillButton
import com.mindfulminutes.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun GuidedEscapesScreen() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf("journeys") }

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
                Brush.verticalGradient(
                    colors = listOf(Surface, Background, Background)
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
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Mix ambient sounds or follow a guided journey.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(24.dp))

        // Tab bar — journeys only (soundscapes removed)

        Spacer(Modifier.height(24.dp))

        // Always show journeys
        JourneysTab(
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

@Composable
private fun EscapeTabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) AccentBg else Surface.copy(alpha = 0.5f),
                RoundedCornerShape(100.dp)
            )
            .border(
                1.dp,
                if (selected) AccentBorder else CardBorder,
                RoundedCornerShape(100.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = if (selected) Accent else TextSecondary,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            letterSpacing = 0.3.sp
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Sound grid
        item {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(300.dp)
            ) {
                items(SOUNDSCAPES) { soundscape ->
                    val isOn = activeVolumes.containsKey(soundscape.id)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isOn) soundscape.color.copy(alpha = 0.1f) else Surface.copy(alpha = 0.5f),
                                RoundedCornerShape(20.dp)
                            )
                            .border(
                                1.dp,
                                if (isOn) soundscape.color.copy(alpha = 0.3f) else CardBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { onToggleSound(soundscape.id) }
                            .padding(vertical = 24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = soundscape.icon,
                                fontSize = 32.sp,
                                modifier = Modifier.alpha(if (isOn) 1f else 0.6f)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = soundscape.name,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 13.sp,
                                color = if (isOn) TextPrimary else TextTertiary,
                                fontWeight = if (isOn) FontWeight.Medium else FontWeight.Normal
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
                        .background(Surface, RoundedCornerShape(20.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "NOW PLAYING · ${activeVolumes.size} SOUND${if (activeVolumes.size > 1) "S" else ""}",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = Accent,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(24.dp))

                        activeVolumes.forEach { (id, vol) ->
                            val soundscape = SOUNDSCAPES.find { it.id == id } ?: return@forEach
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Text(text = soundscape.icon, fontSize = 20.sp, modifier = Modifier.width(32.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = soundscape.name,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    modifier = Modifier.width(100.dp)
                                )
                                Slider(
                                    value = vol,
                                    onValueChange = { onSetVolume(id, it) },
                                    modifier = Modifier.weight(1f).height(24.dp),
                                    colors = SliderDefaults.colors(
                                        thumbColor = soundscape.color,
                                        activeTrackColor = soundscape.color.copy(alpha = 0.4f),
                                        inactiveTrackColor = DividerColor
                                    )
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "${(vol * 100).toInt()}%",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.width(36.dp)
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
                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "SLEEP TIMER",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = Accent,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                                            if (selected) AccentBg else SurfaceLight,
                                            RoundedCornerShape(100.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (selected) AccentBorder else CardBorder,
                                            RoundedCornerShape(100.dp)
                                        )
                                        .clickable { onSetSleepTimer(value) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = label,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 12.sp,
                                        color = if (selected) Accent else TextSecondary
                                    )
                                }
                            }

                            if (sleepLeft > 0) {
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "${sleepLeft / 60}:${(sleepLeft % 60).toString().padStart(2, '0')} left",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = Accent,
                                    fontWeight = FontWeight.Medium
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
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
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
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(ESCAPE_JOURNEYS) { journey ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface, RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                if (activeJourney?.id == journey.id && journeyPhase == "preview") {
                    // Expanded preview
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = journey.icon, fontSize = 28.sp)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = journey.name,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${journey.mins} min · ${journey.steps.size} moments",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = TextTertiary
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        journey.steps.forEachIndexed { i, stepText ->
                            Row(
                                modifier = Modifier.padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(SurfaceLight, CircleShape)
                                        .border(1.dp, CardBorder, CircleShape)
                                ) {
                                    Text(
                                        text = "${i + 1}",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = stepText,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            PillButton(
                                text = "BACK",
                                color = TextSecondary,
                                bgColor = SurfaceLight,
                                borderColor = CardBorder,
                                onClick = { onDeselectJourney() }
                            )
                            PillButton(
                                text = "I'M READY",
                                color = Color.White,
                                bgColor = Accent,
                                borderColor = Accent.copy(alpha = 0.5f),
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
                        Text(text = journey.icon, fontSize = 36.sp)
                        Spacer(Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = journey.name,
                                fontFamily = FontFamily.Serif,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                            Text(
                                text = "${journey.mins} min journey",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = journey.steps.first(),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                color = TextTertiary,
                                lineHeight = 20.sp,
                                fontStyle = FontStyle.Italic
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
    val speechManager = LocalSpeechManager.current
    val progress = if (phase == "complete") 1f else time.toFloat() / (journey.mins * 60)
    val timeLeft = journey.mins * 60 - time
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val currentStep = if (phase == "active" && journey.steps.isNotEmpty()) journey.steps.getOrNull(step) else null
    val completionMsg = "Journey complete. Well done."

    // Speak step text when step changes during active phase
    LaunchedEffect(step, phase) {
        if (phase == "active") {
            currentStep?.let { speechManager?.speak(it) }
        } else if (phase == "complete") {
            speechManager?.speak(completionMsg)
        }
    }

    // Stop speech when overlay is dismissed
    DisposableEffect(Unit) {
        onDispose { speechManager?.stop() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background.copy(alpha = 0.98f))
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
                color = TextTertiary,
                fontSize = 26.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onClose() }
            )

            Spacer(Modifier.weight(1f))

            Text(text = journey.icon, fontSize = 40.sp)
            Spacer(Modifier.height(12.dp))

            Text(
                text = journey.name,
                fontFamily = FontFamily.Serif,
                fontSize = 26.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary
            )

            Spacer(Modifier.height(32.dp))

            // Circular progress
            val trackColor = TextMuted.copy(alpha = 0.1f)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(180.dp)
                    .drawBehind {
                        val strokeWidth = 3.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)

                        drawCircle(
                            color = trackColor,
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = journey.color,
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
                        fontSize = 22.sp,
                        color = journey.color
                    )
                } else {
                    Text(
                        text = "$minutes:${seconds.toString().padStart(2, '0')}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimary
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Current step text
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(100.dp).widthIn(max = 380.dp)
            ) {
                Text(
                    text = if (phase == "complete") "You have returned, carrying peace within you." else journey.steps[step],
                    fontFamily = FontFamily.Serif,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // Step dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                journey.steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (i <= step && phase == "active") journey.color else TextMuted.copy(alpha = 0.2f),
                                CircleShape
                            )
                    )
                }
            }

            // Action buttons
            when (phase) {
                "ready" -> PillButton(
                    text = "BEGIN JOURNEY",
                    color = Color.White,
                    bgColor = Accent,
                    borderColor = Accent.copy(alpha = 0.5f),
                    onClick = onBegin
                )
                "complete" -> PillButton(
                    text = "RETURN",
                    color = Color.White,
                    bgColor = Accent,
                    borderColor = Accent.copy(alpha = 0.5f),
                    onClick = onClose
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
