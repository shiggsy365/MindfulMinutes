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
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import com.mindfulminutes.ZenWidget
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
    var quote by remember { mutableStateOf(Quote("The quieter you become, the more you can hear.", "Ram Dass")) }
    var editIntent by remember { mutableStateOf(false) }
    var tempIntent by remember { mutableStateOf(intention) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = remember { context.getSharedPreferences("mindful_prefs", Context.MODE_PRIVATE) }

    // Breathing animation
    val breatheAnim = rememberInfiniteTransition(label = "breathe")
    val breatheScale by breatheAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheScale"
    )
    val breatheAlpha by breatheAnim.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOut),
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
                    null
                }
            }
            if (result != null) {
                quote = result
                // Save to SharedPrefs so the widget can read it
                prefs.edit()
                    .putString("widget_quote", result.text)
                    .putString("widget_quote_author", result.author)
                    .apply()
                // Refresh the widget
                val mgr = AppWidgetManager.getInstance(context)
                val ids = mgr.getAppWidgetIds(ComponentName(context, ZenWidget::class.java))
                ids.forEach { ZenWidget.updateAppWidget(context, mgr, it) }
            }
        } catch (_: Exception) {}
    }

    // Pick today's background image (cycles daily through 17 images)
    val dayIndex = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR) % 17
    val bgResName = "zen_bg_%02d".format(dayIndex)
    val bgResId = context.resources.getIdentifier(bgResName, "drawable", context.packageName)

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen photo background
        if (bgResId != 0) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = bgResId),
                contentDescription = null,
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Dark gradient scrim for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to Color.Black.copy(alpha = 0.35f),
                            0.4f to Color.Black.copy(alpha = 0.55f),
                            1.0f to Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(Modifier.weight(1f))

            // Breathing center
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(breatheScale)
                    .alpha(breatheAlpha)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Accent.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            Spacer(Modifier.height(48.dp))

            // Quote
            Text(
                text = "\u201C${quote.text}\u201D",
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic,
                color = TextPrimary.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
                modifier = Modifier.widthIn(max = 420.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "— ${quote.author}",
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(64.dp))

            // Intention
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
            ) {
                if (!editIntent) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Surface.copy(alpha = 0.4f),
                                RoundedCornerShape(24.dp)
                            )
                            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                            .clickable {
                                tempIntent = intention
                                editIntent = true
                            }
                            .padding(horizontal = 28.dp, vertical = 24.dp)
                    ) {
                        if (intention.isNotBlank()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "TODAY'S INTENTION",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = Accent,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = intention,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 19.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp
                                )
                            }
                        } else {
                            Text(
                                text = "\uD83C\uDF3F Set a daily intention",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 15.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Surface, RoundedCornerShape(24.dp))
                            .border(1.dp, AccentBorder, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "WHAT'S YOUR INTENTION TODAY?",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            color = Accent,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(16.dp))
                        TextField(
                            value = tempIntent,
                            onValueChange = { if (it.length <= 80) tempIntent = it },
                            placeholder = {
                                Text("Quiet the mind...", fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, color = TextMuted)
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Background,
                                unfocusedContainerColor = Background,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                cursorColor = Accent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontSize = 19.sp,
                                fontStyle = FontStyle.Italic
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                color = TextSecondary,
                                modifier = Modifier
                                    .clickable { editIntent = false }
                                    .padding(8.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .background(Accent.copy(alpha = 0.15f), RoundedCornerShape(100.dp))
                                    .border(1.dp, Accent.copy(alpha = 0.3f), RoundedCornerShape(100.dp))
                                    .clickable {
                                        onSetIntention(tempIntent.trim())
                                        editIntent = false
                                    }
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Set",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 14.sp,
                                    color = Accent,
                                    fontWeight = FontWeight.Medium
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
