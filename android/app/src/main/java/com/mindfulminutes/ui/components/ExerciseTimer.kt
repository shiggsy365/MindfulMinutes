package com.mindfulminutes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.data.*
import com.mindfulminutes.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.min

@Composable
fun ExerciseTimer(
    exercise: Exercise,
    onClose: () -> Unit,
    onFav: (() -> Unit)? = null,
    isFav: Boolean = false,
    onComplete: () -> Unit = {},
    onJournal: (() -> Unit)? = null,
    isMuted: Boolean = false,
    onToggleMute: () -> Unit = {}
) {
    val isBreathing = exercise.cat == "breathing"
    val pattern = if (isBreathing) getBreathingPattern(exercise) else null
    val cat = getCategoryForExercise(exercise)

    var customMins by remember { mutableIntStateOf(exercise.mins) }
    var phase by remember { mutableStateOf("preview") }
    var timeLeft by remember { mutableIntStateOf(customMins * 60) }
    var cyclesLeft by remember { mutableIntStateOf(exercise.cycles ?: 0) }
    var step by remember { mutableIntStateOf(0) }
    var elapsed by remember { mutableIntStateOf(0) }

    val speechManager = com.mindfulminutes.LocalSpeechManager.current
    var lastSpokenInstruction by remember { mutableStateOf("") }

    // Reset time when customMins changes in preview
    LaunchedEffect(customMins, phase) {
        if (phase == "preview" || phase == "ready") {
            timeLeft = customMins * 60
        }
    }

    // Timer
    LaunchedEffect(phase) {
        if (phase == "active") {
            elapsed = 0
            while ((exercise.cycles == null && timeLeft > 0) || (exercise.cycles != null && cyclesLeft > 0)) {
                delay(1000)
                elapsed++
                if (exercise.cycles == null) {
                    timeLeft--
                } else if (pattern != null) {
                    val cycleDuration = pattern.inhale + pattern.hold1 + pattern.exhale + pattern.hold2
                    if (elapsed % cycleDuration == 0) {
                        cyclesLeft--
                    }
                }
                
                if ((exercise.cycles == null && timeLeft <= 0) || (exercise.cycles != null && cyclesLeft <= 0)) {
                    phase = "complete"
                    onComplete()
                }
            }
        }
    }

    // Step calculation
    LaunchedEffect(timeLeft, phase) {
        if (phase == "active" && !isBreathing) {
            val stepDuration = (customMins * 60f) / exercise.steps.size
            step = min(
                ((customMins * 60 - timeLeft) / stepDuration).toInt(),
                exercise.steps.size - 1
            )
        }
    }

    val progress = when (phase) {
        "preview", "ready" -> 0f
        "complete" -> 1f
        else -> (customMins * 60 - timeLeft).toFloat() / (customMins * 60)
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    val currentInstruction = if (isBreathing && pattern != null && phase == "active") {
        val phaseInfo = getBreathPhase(pattern, elapsed)
        pattern.instructions[phaseInfo.phase] ?: ""
    } else {
        exercise.steps.getOrElse(step) { "" }
    }

    // TTS Logic
    LaunchedEffect(currentInstruction, phase) {
        if (phase == "active" && currentInstruction.isNotEmpty() && currentInstruction != lastSpokenInstruction) {
            speechManager?.speak(currentInstruction)
            lastSpokenInstruction = currentInstruction
        } else if (phase == "complete" && lastSpokenInstruction != "COMPLETE_DONE") {
            speechManager?.speak("Activity complete. well done.")
            lastSpokenInstruction = "COMPLETE_DONE"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Surface, Background, SurfaceLight)
                )
            )
    ) {
        when (phase) {
            "preview" -> PreviewPhase(
                exercise = exercise,
                cat = cat,
                isBreathing = isBreathing,
                pattern = pattern,
                onFav = onFav,
                isFav = isFav,
                isMuted = isMuted,
                onToggleMute = onToggleMute,
                onReady = { phase = "ready" },
                onClose = {
                    speechManager?.stop()
                    onClose()
                }
            )
            "ready" -> ReadyPhase(
                exercise = exercise,
                cat = cat,
                onClose = onClose,
                onReview = { phase = "preview" },
                onBegin = {
                    timeLeft = (exercise.mins).coerceAtLeast(1) * 60
                    cyclesLeft = exercise.cycles ?: 0
                    elapsed = 0
                    step = 0
                    phase = "active"
                }
            )
            else -> ActiveCompletePhase(
                exercise = exercise,
                cat = cat,
                phase = phase,
                isBreathing = isBreathing,
                pattern = pattern,
                elapsed = elapsed,
                progress = progress,
                minutes = minutes,
                seconds = seconds,
                currentInstruction = currentInstruction,
                step = step,
                cyclesLeft = cyclesLeft,
                isMuted = isMuted,
                onToggleMute = onToggleMute,
                onClose = {
                    speechManager?.stop()
                    onClose()
                },
                onReset = {
                    speechManager?.stop()
                    timeLeft = (exercise.mins).coerceAtLeast(1) * 60
                    cyclesLeft = exercise.cycles ?: 0
                    step = 0
                    elapsed = 0
                    phase = "ready"
                    lastSpokenInstruction = ""
                },
                onJournal = onJournal
            )
        }
    }
}

@Composable
private fun PreviewPhase(
    exercise: Exercise,
    cat: Category,
    isBreathing: Boolean,
    pattern: BreathingPattern?,
    onClose: () -> Unit,
    onFav: (() -> Unit)?,
    isFav: Boolean,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onReady: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Close + Fav buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (onFav != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isFav) "♥" else "♡",
                        color = if (isFav) FavColor else TextMuted,
                        fontSize = 22.sp,
                        modifier = Modifier.clickable { onFav() }
                    )
                    Spacer(Modifier.width(16.dp))
                    MuteToggle(isMuted = isMuted, onToggle = onToggleMute)
                }
            } else {
                MuteToggle(isMuted = isMuted, onToggle = onToggleMute)
            }
            Text(
                text = "✕",
                color = TextTertiary,
                fontSize = 26.sp,
                modifier = Modifier.clickable { onClose() }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Category label
        Text(
            text = "${cat.icon} ${cat.name}".uppercase(),
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = cat.color.copy(alpha = 0.7f),
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(8.dp))

        // Exercise name
        Text(
            text = exercise.name,
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // Fixed duration display
        Text(
            text = "${exercise.mins} MINUTE ACTIVITY",
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            color = Accent,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(24.dp))

        if (isBreathing && pattern != null) {
            // Pattern info
        }

        Spacer(Modifier.height(32.dp))

        // Steps preview
        Text(
            text = if (isBreathing) "HOW IT WORKS" else "WHAT YOU'LL DO",
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = TextMuted,
            letterSpacing = 1.5.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(16.dp))

        exercise.steps.forEachIndexed { i, stepText ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .background(cat.bg, CircleShape)
                        .border(1.dp, cat.border, CircleShape)
                ) {
                    Text(
                        text = "${i + 1}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = cat.color
                    )
                }
                Text(
                    text = stepText,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 22.sp,
                    modifier = Modifier.weight(1f).padding(top = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Ready button
        PillButton(
            text = "I'M READY",
            color = cat.color,
            bgColor = cat.bg,
            borderColor = cat.border,
            onClick = onReady
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ReadyPhase(
    exercise: Exercise,
    cat: Category,
    onClose: () -> Unit,
    onReview: () -> Unit,
    onBegin: () -> Unit
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

        Text(
            text = "${cat.icon} ${cat.name}".uppercase(),
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = cat.color.copy(alpha = 0.7f),
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = exercise.name,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary
        )

        Spacer(Modifier.height(32.dp))

        // Breathing circle animation placeholder
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(1.5.dp, cat.color.copy(alpha = 0.3f), CircleShape)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Find a comfortable position. When you're settled, begin.",
            fontFamily = FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Light,
            fontStyle = FontStyle.Italic,
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,
            modifier = Modifier.widthIn(max = 320.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "${exercise.mins} min",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PillButton(
                text = "REVIEW",
                color = TextTertiary,
                bgColor = CardBg,
                borderColor = CardBorder,
                onClick = onReview
            )
            PillButton(
                text = "BEGIN",
                color = cat.color,
                bgColor = cat.bg,
                borderColor = cat.border,
                onClick = onBegin
            )
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ActiveCompletePhase(
    exercise: Exercise,
    cat: Category,
    phase: String,
    isBreathing: Boolean,
    pattern: BreathingPattern?,
    elapsed: Int,
    progress: Float,
    minutes: Int,
    seconds: Int,
    currentInstruction: String,
    step: Int,
    cyclesLeft: Int,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onClose: () -> Unit,
    onReset: () -> Unit,
    onJournal: (() -> Unit)?
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            MuteToggle(isMuted = isMuted, onToggle = onToggleMute)
        }

        Spacer(Modifier.weight(1f))

        Text(
            text = "${cat.icon} ${cat.name}".uppercase(),
            fontFamily = FontFamily.SansSerif,
            fontSize = 10.sp,
            color = cat.color.copy(alpha = 0.7f),
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = exercise.name,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )

        Spacer(Modifier.height(24.dp))

        // Timer/Progress at Top
        if (exercise.mins > 0) {
            val trackColor = TextMuted.copy(alpha = 0.1f)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)

                        drawCircle(
                            color = trackColor,
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = cat.color,
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
                        fontSize = 16.sp,
                        color = cat.color
                    )
                } else {
                    Text(
                        text = "$minutes:${seconds.toString().padStart(2, '0')}",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        val showAllInstructions = exercise.showAllSteps || !isBreathing

        if (phase == "active" && !showAllInstructions) {
            // Breathing guide for guided breathing
            if (isBreathing && pattern != null) {
                Spacer(Modifier.weight(0.5f))
                BreathingGuide(pattern = pattern, elapsed = elapsed)
                Spacer(Modifier.weight(0.5f))
            }
        }

        if (showAllInstructions && phase == "active") {
            // Show all instructions in a scrollable area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp)
            ) {
                exercise.steps.forEachIndexed { i, stepText ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "${i + 1}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            color = cat.color.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Light
                        )
                        Text(
                            text = stepText,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 16.sp,
                            color = TextSecondary,
                            lineHeight = 24.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        } else {
            // Current instruction for guided mode
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(80.dp).widthIn(max = 360.dp)
            ) {
                Text(
                    text = if (phase == "complete") "You did beautifully. Carry this stillness with you." else currentInstruction,
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
            }
            Spacer(Modifier.weight(1f))
        }

        // Cycle count for breathing (cycle-limited)
        if (isBreathing && pattern != null && exercise.cycles != null && phase == "active") {
            Spacer(Modifier.height(12.dp))
            val currentCycle = min(exercise.cycles - cyclesLeft + 1, exercise.cycles)
            Text(
                text = "Cycle $currentCycle / ${exercise.cycles}",
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = TextTertiary,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        // Action buttons
        if (phase == "active") {
            PillButton(
                text = "RESET",
                color = TextTertiary,
                bgColor = CardBg,
                borderColor = CardBorder,
                onClick = onReset
            )
        }
        if (phase == "complete") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (onJournal != null) {
                    PillButton(
                        text = "\uD83D\uDCDD JOURNAL",
                        color = TextTertiary,
                        bgColor = CardBg,
                        borderColor = CardBorder,
                        onClick = onJournal
                    )
                }
                PillButton(
                    text = "RETURN",
                    color = cat.color,
                    bgColor = cat.bg,
                    borderColor = cat.border,
                    onClick = onClose
                )
            }
        }

        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun PillButton(
    text: String,
    color: Color,
    bgColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(100.dp))
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(horizontal = 32.dp, vertical = 14.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = color,
            letterSpacing = 1.5.sp
        )
    }
}
