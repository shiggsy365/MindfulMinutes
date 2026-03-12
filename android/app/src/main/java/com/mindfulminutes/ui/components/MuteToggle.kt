package com.mindfulminutes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.ui.theme.TextMuted
import com.mindfulminutes.ui.theme.TextTertiary

@Composable
fun MuteToggle(
    isMuted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isMuted) "🔇" else "🔊",
        color = if (isMuted) TextTertiary else TextMuted,
        fontSize = 22.sp,
        modifier = modifier
            .clickable { onToggle() }
            .padding(4.dp)
    )
}
