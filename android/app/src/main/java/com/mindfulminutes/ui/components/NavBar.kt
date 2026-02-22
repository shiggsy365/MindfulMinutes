package com.mindfulminutes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.ui.theme.*

data class NavItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun NavBar(
    activePage: String,
    onPageChange: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val pages = listOf(
        NavItem("zen", "Zen", Icons.Outlined.Public),
        NavItem("minutes", "Minutes", Icons.Outlined.Timer),
        NavItem("mood", "Mood", Icons.Outlined.FavoriteBorder),
        NavItem("escapes", "Escapes", Icons.Outlined.Headphones),
        NavItem("practice", "Practice", Icons.Outlined.BarChart)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xE00A0F0C))
            .padding(
                horizontal = 4.dp,
                vertical = 8.dp
            )
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pages.forEach { page ->
            val isActive = activePage == page.id
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onPageChange(page.id) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = page.label,
                    tint = if (isActive) Accent else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = page.label.uppercase(),
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 8.sp,
                    fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal,
                    color = if (isActive) Accent else TextMuted,
                    letterSpacing = 0.8.sp
                )
                if (isActive) {
                    Spacer(Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(Accent.copy(alpha = 0.6f), CircleShape)
                    )
                }
            }
        }

        // Settings button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable { onSettingsClick() }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "CONFIG",
                fontFamily = FontFamily.SansSerif,
                fontSize = 8.sp,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
        }
    }
}
