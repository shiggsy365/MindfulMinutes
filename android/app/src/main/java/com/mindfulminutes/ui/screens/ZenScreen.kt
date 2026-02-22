package com.mindfulminutes.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

data class Quote(val text: String, val author: String)

@Composable
fun ZenScreen(
    intention: String,
    onSetIntention: (String) -> Unit
) {
    var quote by remember { mutableStateOf(Quote("In the middle of difficulty lies opportunity.", "Albert Einstein")) }
    var editIntent by remember { mutableStateOf(false) }
    var tempIntent by remember { mutableStateOf(intention) }

    // Breathing animation
    val breatheAnim = rememberInfiniteTransition(label = "breathe")
    val breatheScale by breatheAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )
    val breatheAlpha by breatheAnim.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheAlpha"
    )

    // Fetch quote
    LaunchedEffect(Unit) {
        try {
            val result = withContext(Dispatchers.IO) {
                try {
                    val response = URL("https://zenquotes.io/api/random").readText()
                    val arr = JSONArray(response)
                    val obj = arr.getJSONObject(0)
                    Quote(obj.getString("q"), obj.getString("a"))
                } catch (_: Exception) {
                    try {
                        val response = URL("https://api.adviceslip.com/advice").readText()
                        val obj = JSONObject(response).getJSONObject("slip")
                        Quote(obj.getString("advice"), "Advice Slip")
                    } catch (_: Exception) {
                        null
                    }
                }
            }
            if (result != null) quote = result
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1A15),
                        Color(0xFF0A0F0C),
                        Color(0xFF0D1510)
                    )
                )
            )
    ) {
        // Background stars decoration
        Box(modifier = Modifier.fillMaxSize()) {
            repeat(30) { i ->
                val x = (i * 37 + 13) % 100
                val y = (i * 23 + 7) % 50
                val size = ((i * 7 + 3) % 3 + 1).toFloat()
                val alpha = ((i * 13 + 5) % 60 + 20) / 100f
                Box(
                    modifier = Modifier
                        .offset(
                            x = (x * 3.6).dp,
                            y = (y * 3).dp
                        )
                        .size(size.dp)
                        .alpha(alpha)
                        .background(Color.White, CircleShape)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(Modifier.weight(1f))

            // Breathing circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .scale(breatheScale)
                    .alpha(breatheAlpha)
                    .border(1.dp, Accent.copy(alpha = 0.25f), CircleShape)
            )

            Spacer(Modifier.height(32.dp))

            // Quote
            Text(
                text = "\u201C${quote.text}\u201D",
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic,
                color = Color.White.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.widthIn(max = 420.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "— ${quote.author}",
                fontFamily = FontFamily.SansSerif,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.3f),
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(48.dp))

            // Intention section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 360.dp)
            ) {
                if (!editIntent) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White.copy(alpha = 0.04f),
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                tempIntent = intention
                                editIntent = true
                            }
                            .padding(horizontal = 24.dp, vertical = 14.dp)
                    ) {
                        if (intention.isNotBlank()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "TODAY'S INTENTION",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 9.sp,
                                    color = Accent.copy(alpha = 0.5f),
                                    letterSpacing = 1.5.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = intention,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 16.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Text(
                                text = "✦ Set a daily intention",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.3f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White.copy(alpha = 0.04f),
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                Color.White.copy(alpha = 0.08f),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "WHAT'S YOUR INTENTION TODAY?",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 9.sp,
                            color = Accent.copy(alpha = 0.5f),
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        TextField(
                            value = tempIntent,
                            onValueChange = { if (it.length <= 80) tempIntent = it },
                            placeholder = {
                                Text(
                                    "Be patient with myself\u2026",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = TextMuted
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.03f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
                                focusedTextColor = Color.White.copy(alpha = 0.8f),
                                unfocusedTextColor = Color.White.copy(alpha = 0.8f),
                                cursorColor = Accent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = TextTertiary,
                                modifier = Modifier
                                    .clickable { editIntent = false }
                                    .padding(8.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .background(AccentBg, RoundedCornerShape(100.dp))
                                    .border(1.dp, AccentBorder, RoundedCornerShape(100.dp))
                                    .clickable {
                                        onSetIntention(tempIntent.trim())
                                        editIntent = false
                                    }
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Set",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = Accent
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
