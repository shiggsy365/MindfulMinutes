package com.mindfulminutes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.data.JOURNAL_PROMPTS
import com.mindfulminutes.ui.theme.*

@Composable
fun JournalOverlay(
    exerciseName: String?,
    onClose: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val prompt = remember { JOURNAL_PROMPTS.random() }

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

            Text(
                text = "JOURNAL ENTRY",
                fontFamily = FontFamily.SansSerif,
                fontSize = 10.sp,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )

            if (exerciseName != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "After: $exerciseName",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    color = Accent.copy(alpha = 0.6f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = prompt,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic,
                color = TextSecondary.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier.widthIn(max = 320.dp)
            )

            Spacer(Modifier.height(24.dp))

            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = {
                    Text(
                        "Write freely...",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardBg,
                    unfocusedContainerColor = CardBg,
                    focusedTextColor = TextSecondary,
                    unfocusedTextColor = TextSecondary,
                    cursorColor = Accent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(14.dp),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PillButton(
                    text = "SKIP",
                    color = TextTertiary,
                    bgColor = CardBg,
                    borderColor = CardBorder,
                    onClick = onClose
                )
                PillButton(
                    text = "SAVE ENTRY",
                    color = Accent,
                    bgColor = AccentBg,
                    borderColor = AccentBorder,
                    onClick = {
                        if (text.isNotBlank()) onSave(text.trim())
                        onClose()
                    }
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
