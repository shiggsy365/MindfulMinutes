package com.mindfulminutes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlin.math.min

@Composable
fun MoodTimer(
    session: MoodSession,
    mood: Mood,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    onClose: () -> Unit
) {
    var phase by remember { mutableStateOf("ready") }
    var timeLeft by remember { mutableIntStateOf(session.mins * 60) }
    var step by remember { mutableIntStateOf(0) }
    val totalTime = session.mins * 60
    
    val speechManager = com.mindfulminutes.LocalSpeechManager.current
    var lastSpokenStep by remember { mutableIntStateOf(-1) }

    // Timer
    LaunchedEffect(phase) {
        if (phase == "active") {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
                if (timeLeft <= 0) {
                    phase = "complete"
                }
            }
        }
    }

    // Step calculation and TTS
    LaunchedEffect(timeLeft, phase) {
        if (phase == "active") {
            val stepDuration = totalTime.toFloat() / session.steps.size
            step = min(
                ((totalTime - timeLeft) / stepDuration).toInt(),
                session.steps.size - 1
            )
            
            if (step != lastSpokenStep) {
                speechManager?.speak(session.steps[step])
                lastSpokenStep = step
            }
        } else if (phase == "complete" && lastSpokenStep != -2) {
            speechManager?.speak("Well done. You showed up for yourself.")
            lastSpokenStep = -2
        }
    }

    val progress = when (phase) {
        "ready" -> 0f
        "complete" -> 1f
        else -> (totalTime - timeLeft).toFloat() / totalTime
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val meta = LEN_META[session.length]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(mood.bg.copy(alpha = 0.8f), Background)
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mood Session".uppercase(),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Feeling ${mood.name}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 18.sp,
                        color = mood.color
                    )
                }
                Text(
                    text = "✕",
                    color = TextMuted,
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { 
                        speechManager?.stop()
                        onClose() 
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                MuteToggle(isMuted = isMuted, onToggle = onToggleMute)
            }

            Spacer(Modifier.height(32.dp))

            // Circular timer at top
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .drawBehind {
                        val strokeWidth = 4.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        val center = Offset(size.width / 2, size.height / 2)

                        drawCircle(
                            color = Color.White.copy(alpha = 0.05f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = mood.color.copy(alpha = 0.8f),
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
                        text = mood.emoji,
                        fontSize = 48.sp
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$minutes:${seconds.toString().padStart(2, '0')}",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            color = TextPrimary
                        )
                        Text(
                            text = if (phase == "ready") "READY" else "REMAINING",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = TextMuted,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Session Title
            Text(
                text = session.label,
                fontFamily = FontFamily.Serif,
                fontSize = 26.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            
            Spacer(Modifier.height(24.dp))

            // Instructions Area (Maximised)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(CardBg.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    items(session.steps.size) { i ->
                        val isCurrent = i == step && phase == "active"
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        if (isCurrent) mood.color.copy(alpha = 0.2f) else Color.Transparent,
                                        CircleShape
                                    )
                                    .border(1.dp, if (isCurrent) mood.color else TextMuted, CircleShape)
                            ) {
                                Text(
                                    text = (i + 1).toString(),
                                    fontSize = 10.sp,
                                    color = if (isCurrent) mood.color else TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = session.steps[i],
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                color = if (isCurrent) TextPrimary else TextTertiary,
                                fontStyle = if (isCurrent) FontStyle.Normal else FontStyle.Italic,
                                lineHeight = 24.sp
                            )
                        }
                    }
                    
                    if (phase == "complete") {
                        item {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "You showed up for yourself. Be well.",
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                color = mood.color,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Action buttons
            when (phase) {
                "ready" -> PillButton(
                    text = "BEGIN SESSION",
                    color = mood.color,
                    bgColor = mood.bg,
                    borderColor = mood.border,
                    onClick = { phase = "active" }
                )
                "active" -> PillButton(
                    text = "RESET",
                    color = TextTertiary,
                    bgColor = CardBg,
                    borderColor = CardBorder,
                    onClick = {
                        speechManager?.stop()
                        timeLeft = totalTime
                        step = 0
                        phase = "ready"
                        lastSpokenStep = -1
                    }
                )
                "complete" -> PillButton(
                    text = "FINISH",
                    color = mood.color,
                    bgColor = mood.bg,
                    borderColor = mood.border,
                    onClick = onClose
                )
            }
        }
    }
}
