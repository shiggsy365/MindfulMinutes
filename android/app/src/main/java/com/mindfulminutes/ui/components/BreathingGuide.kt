package com.mindfulminutes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.data.BreathingPattern
import com.mindfulminutes.data.getBreathPhase
import com.mindfulminutes.ui.theme.*

@Composable
fun BreathingGuide(pattern: BreathingPattern, elapsed: Int) {
    val phaseInfo = getBreathPhase(pattern, elapsed)

    val targetScale = when (phaseInfo.phase) {
        "inhale" -> 1f + phaseInfo.progress * 0.45f
        "exhale" -> 1.45f - phaseInfo.progress * 0.45f
        "hold1" -> 1.45f
        else -> 1f
    }

    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 1000),
        label = "breathScale"
    )

    val phaseColor = when (phaseInfo.phase) {
        "inhale" -> InhaleColor
        "exhale" -> ExhaleColor
        else -> HoldColor
    }

    val color by animateColorAsState(
        targetValue = phaseColor,
        animationSpec = tween(durationMillis = 500),
        label = "breathColor"
    )

    val phaseLabel = when (phaseInfo.phase) {
        "inhale" -> "↑ Inhale"
        "exhale" -> "↓ Exhale"
        else -> "· Hold ·"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .scale(scale)
                .background(color.copy(alpha = 0.12f), CircleShape)
                .border(1.5.dp, color.copy(alpha = 0.35f), CircleShape)
        ) {
            Text(
                text = "${phaseInfo.countdown}",
                fontFamily = FontFamily.SansSerif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        Text(
            text = phaseLabel,
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = color.copy(alpha = 0.9f),
            letterSpacing = 1.5.sp
        )
    }
}
