package com.mindfulminutes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
    onClose: () -> Unit
) {
    var phase by remember { mutableStateOf("ready") }
    var timeLeft by remember { mutableIntStateOf(session.mins * 60) }
    var step by remember { mutableIntStateOf(0) }
    val totalTime = session.mins * 60

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

    // Step calculation
    LaunchedEffect(timeLeft, phase) {
        if (phase == "active") {
            val stepDuration = totalTime.toFloat() / session.steps.size
            step = min(
                ((totalTime - timeLeft) / stepDuration).toInt(),
                session.steps.size - 1
            )
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
            .background(Color(0xF5080C0A))
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

            Text(text = mood.emoji, fontSize = 32.sp)
            Spacer(Modifier.height(8.dp))

            Text(
                text = "${meta?.icon ?: ""} ${meta?.label ?: ""}".uppercase(),
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = mood.color.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))

            Text(
                text = session.label,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary
            )

            Spacer(Modifier.height(32.dp))

            // Circular timer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
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
                            color = mood.color.copy(alpha = 0.7f),
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
                        text = "be well",
                        fontFamily = FontFamily.Serif,
                        fontSize = 22.sp,
                        color = mood.color
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$minutes:${seconds.toString().padStart(2, '0')}",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 44.sp,
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

            // Current step text
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(90.dp).widthIn(max = 360.dp)
            ) {
                Text(
                    text = if (phase == "complete") "You showed up for yourself." else session.steps[step],
                    fontFamily = FontFamily.Serif,
                    fontSize = 18.sp,
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
                session.steps.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                if (i <= step && phase != "ready") mood.color else Color.White.copy(alpha = 0.1f),
                                CircleShape
                            )
                    )
                }
            }

            // Action buttons
            when (phase) {
                "ready" -> PillButton(
                    text = "BEGIN",
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
                        timeLeft = totalTime
                        step = 0
                        phase = "ready"
                    }
                )
                "complete" -> PillButton(
                    text = "RETURN",
                    color = mood.color,
                    bgColor = mood.bg,
                    borderColor = mood.border,
                    onClick = onClose
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
