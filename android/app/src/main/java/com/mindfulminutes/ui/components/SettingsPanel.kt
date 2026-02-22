package com.mindfulminutes.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mindfulminutes.ui.theme.*

@Composable
fun SettingsPanel(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    apiKey: String,
    onSaveKey: (String) -> Unit,
    notifications: Map<String, Boolean>,
    onToggleNotification: (String) -> Unit
) {
    if (!isOpen) return

    var tempKey by remember(apiKey) { mutableStateOf(apiKey) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xF2121814), RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Settings",
                    fontFamily = FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary
                )

                Spacer(Modifier.height(24.dp))

                // Unsplash Key
                Text(
                    text = "UNSPLASH KEY",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    color = TextTertiary,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = tempKey,
                    onValueChange = { tempKey = it },
                    placeholder = {
                        Text("Access Key", fontFamily = FontFamily.SansSerif, fontSize = 13.sp, color = TextMuted)
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Accent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(10.dp)),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp
                    )
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AccentBg, RoundedCornerShape(10.dp))
                        .border(1.dp, AccentBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            onSaveKey(tempKey)
                            onDismiss()
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "Save Key",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        color = Accent,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Reminders
                Text(
                    text = "REMINDERS",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    color = TextTertiary,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(12.dp))

                val reminders = listOf(
                    Triple("morning", "Morning check-in · 8am", notifications["morning"] ?: false),
                    Triple("afternoon", "Afternoon pause · 1pm", notifications["afternoon"] ?: false),
                    Triple("evening", "Evening wind-down · 9pm", notifications["evening"] ?: false)
                )

                reminders.forEach { (key, label, isOn) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardBg, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = label,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        // Toggle switch
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(22.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(
                                    if (isOn) Accent.copy(alpha = 0.4f)
                                    else Color.White.copy(alpha = 0.1f)
                                )
                                .clickable { onToggleNotification(key) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .size(16.dp)
                                    .offset(x = if (isOn) 18.dp else 0.dp)
                                    .background(
                                        if (isOn) Accent else Color.White.copy(alpha = 0.3f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
