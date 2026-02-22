package com.mindfulminutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.data.*
import com.mindfulminutes.ui.theme.*
import java.util.Calendar

@Composable
fun StatsScreen(
    stats: List<StatEntry>,
    journal: List<JournalEntry>,
    favourites: List<String>
) {
    var tab by remember { mutableStateOf("stats") }

    // Calculate stats
    val totalMins = stats.sumOf { it.mins }
    val totalSessions = stats.size
    val catCounts = mutableMapOf<String, Int>()
    stats.forEach { catCounts[it.cat] = (catCounts[it.cat] ?: 0) + 1 }
    val topCat = catCounts.entries.maxByOrNull { it.value }

    val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val last7 = stats.filter {
        val d = sdf.parse(it.date)
        d != null && Calendar.getInstance().apply { time = d } >= weekAgo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0E100F), Background)))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        // Header
        Text(
            text = "Your Practice",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Stats, journal, and your mindfulness journey.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Tab bar
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatsTabChip("\uD83D\uDCCA Stats", tab == "stats") { tab = "stats" }
            Spacer(Modifier.width(8.dp))
            StatsTabChip("\uD83D\uDCDD Journal", tab == "journal") { tab = "journal" }
            Spacer(Modifier.width(8.dp))
            StatsTabChip("♥ Favourites", tab == "favourites") { tab = "favourites" }
        }

        Spacer(Modifier.height(20.dp))

        when (tab) {
            "stats" -> StatsTab(
                totalMins = totalMins,
                totalSessions = totalSessions,
                thisWeek = last7.size,
                topCat = topCat,
                catCounts = catCounts
            )
            "journal" -> JournalTab(journal = journal)
            "favourites" -> FavouritesTab(favourites = favourites)
        }
    }
}

@Composable
private fun StatsTabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) CardBgHover else Color.Transparent,
                RoundedCornerShape(100.dp)
            )
            .border(
                1.dp,
                if (selected) Color.White.copy(alpha = 0.15f) else CardBorder,
                RoundedCornerShape(100.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = if (selected) TextPrimary else TextTertiary,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun StatsTab(
    totalMins: Int,
    totalSessions: Int,
    thisWeek: Int,
    topCat: Map.Entry<String, Int>?,
    catCounts: Map<String, Int>
) {
    val topCategory = topCat?.let { CATEGORIES.find { c -> c.id == it.key } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Stat cards grid
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard("⏱\uFE0F", "$totalMins", "Total Minutes", Modifier.weight(1f))
                StatCard("\uD83E\uDDD8", "$totalSessions", "Sessions Done", Modifier.weight(1f))
            }
        }
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard("\uD83D\uDCC5", "$thisWeek", "This Week", Modifier.weight(1f))
                StatCard(
                    topCategory?.icon ?: "—",
                    topCategory?.name ?: "—",
                    "Top Category",
                    Modifier.weight(1f)
                )
            }
        }

        // Category breakdown
        if (catCounts.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "CATEGORY BREAKDOWN",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 9.sp,
                            color = TextMuted,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(16.dp))

                        CATEGORIES.forEach { cat ->
                            val count = catCounts[cat.id] ?: 0
                            val pct = if (totalSessions > 0) count.toFloat() / totalSessions else 0f

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Text(text = cat.icon, fontSize = 14.sp, modifier = Modifier.width(24.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = cat.name,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.width(80.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(3.dp))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(pct)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(cat.color)
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "$count",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    modifier = Modifier.width(24.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }

        // Empty state
        if (totalSessions == 0) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Text(text = "\uD83C\uDF31", fontSize = 32.sp)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Complete your first exercise to start tracking stats.",
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextTertiary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(icon: String, value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Serif,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label.uppercase(),
                fontFamily = FontFamily.SansSerif,
                fontSize = 9.sp,
                color = TextMuted,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
private fun JournalTab(journal: List<JournalEntry>) {
    if (journal.isNotEmpty()) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(journal.reversed()) { entry ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = entry.exercise ?: "Free writing",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 10.sp,
                                color = Accent.copy(alpha = 0.5f),
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = entry.date,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = entry.text,
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Text(text = "\uD83D\uDCDD", fontSize = 32.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Journal entries appear here after exercises.",
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                fontStyle = FontStyle.Italic,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FavouritesTab(favourites: List<String>) {
    if (favourites.isNotEmpty()) {
        val favExercises = EXERCISES.filter { it.id in favourites }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(favExercises) { exercise ->
                val cat = getCategoryForExercise(exercise)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${cat.icon} ${cat.name}".uppercase(),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 10.sp,
                                color = cat.color.copy(alpha = 0.7f),
                                letterSpacing = 1.2.sp
                            )
                            Text(
                                text = "${exercise.mins} min",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = exercise.name,
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            Text(text = "♥", fontSize = 32.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tap the heart on any exercise to save it here.",
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                fontStyle = FontStyle.Italic,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}
