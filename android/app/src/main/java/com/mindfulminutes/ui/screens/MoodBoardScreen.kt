package com.mindfulminutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.mindfulminutes.ui.components.MoodTimer
import com.mindfulminutes.ui.components.PillButton
import com.mindfulminutes.ui.theme.*
import java.util.Calendar
import java.util.Date

@Composable
fun MoodBoardScreen(
    navigateTo: (String) -> Unit,
    isMuted: Boolean,
    onToggleMute: () -> Unit
) {
    var tab by remember { mutableStateOf("sessions") }
    var selectedMoodId by remember { mutableStateOf<String?>(null) }
    var activeSession by remember { mutableStateOf<MoodSession?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }

    // Mood logs - seed with some demo data
    var logs by remember {
        mutableStateOf(buildMap {
            val now = Calendar.getInstance()
            val moods = listOf("great", "good", "okay", "low", "rough")
            for (i in 13 downTo 1) {
                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
                val key = dateKey(cal.time)
                val dayLogs = mutableMapOf<String, String>()
                if (Math.random() > 0.15) dayLogs["morning"] = moods.random()
                if (Math.random() > 0.1) dayLogs["afternoon"] = moods.random()
                if (Math.random() > 0.2) dayLogs["evening"] = moods.random()
                if (dayLogs.isNotEmpty()) put(key, dayLogs)
            }
        })
    }

    val mood = selectedMoodId?.let { id -> MOODS.find { it.id == id } }
    val todayKey = dateKey(selectedDate)
    val todayLogs = logs[todayKey] ?: emptyMap()
    val insights = generateInsights(logs)

    // Week data for chart
    val weekData = remember(logs) {
        (6 downTo 0).map { i ->
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            val key = dateKey(cal.time)
            val dayLogs = logs[key] ?: emptyMap()
            val scores = dayLogs.values.mapNotNull { id -> TRACKER_MOODS.find { it.id == id }?.score }
            Triple(dayName(cal.time), if (scores.isNotEmpty()) scores.average().toFloat() else null, cal.time)
        }
    }

    // Active mood timer overlay
    if (activeSession != null && mood != null) {
        MoodTimer(
            session = activeSession!!,
            mood = mood,
            isMuted = isMuted,
            onToggleMute = onToggleMute,
            onClose = { activeSession = null }
        )
        return
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
            text = "Mood Board",
            fontFamily = FontFamily.Serif,
            fontSize = 28.sp,
            fontWeight = FontWeight.Light,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Track, discover patterns, find guided support.",
            fontFamily = FontFamily.SansSerif,
            fontSize = 13.sp,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        // Tab bar
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            TabChip("\uD83D\uDCCA Tracker", tab == "tracker") { tab = "tracker"; selectedMoodId = null }
            TabChip("\uD83E\uDDD8 Sessions", tab == "sessions") { tab = "sessions"; selectedMoodId = null }
            TabChip("\uD83D\uDCA1 Insights", tab == "insights") { tab = "insights"; selectedMoodId = null }
            Spacer(Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // Tab content
        when (tab) {
            "tracker" -> TrackerTab(
                selectedDate = selectedDate,
                todayKey = todayKey,
                todayLogs = todayLogs,
                weekData = weekData,
                onDateChange = { selectedDate = it },
                onLogMood = { slot, moodId ->
                    logs = logs.toMutableMap().apply {
                        val dayLogs = (this[todayKey] ?: emptyMap()).toMutableMap()
                        dayLogs[slot] = moodId
                        this[todayKey] = dayLogs
                    }
                }
            )
            "sessions" -> SessionsTab(
                selectedMoodId = selectedMoodId,
                mood = mood,
                onSelectMood = { selectedMoodId = it },
                onStartSession = { activeSession = it }
            )
            "insights" -> InsightsTab(
                insights = insights,
                onAction = { action ->
                    if (action == "sessions") tab = "sessions"
                    else navigateTo(action)
                }
            )
        }
    }
}

@Composable
private fun TabChip(text: String, selected: Boolean, onClick: () -> Unit) {
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
private fun TrackerTab(
    selectedDate: Date,
    todayKey: String,
    todayLogs: Map<String, String>,
    weekData: List<Triple<String, Float?, Date>>,
    onDateChange: (Date) -> Unit,
    onLogMood: (String, String) -> Unit
) {
    val isToday = todayKey == dateKey()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Date selector
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "‹",
                    fontSize = 20.sp,
                    color = TextTertiary,
                    modifier = Modifier.clickable {
                        val cal = Calendar.getInstance().apply { time = selectedDate; add(Calendar.DAY_OF_YEAR, -1) }
                        onDateChange(cal.time)
                    }.padding(16.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isToday) "Today" else "${dayName(selectedDate)}, ${Calendar.getInstance().apply { time = selectedDate }.get(Calendar.DAY_OF_MONTH)} ${monthName(selectedDate)}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light,
                        color = TextPrimary
                    )
                    if (!isToday) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Jump to today",
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 10.sp,
                            color = Accent.copy(alpha = 0.6f),
                            modifier = Modifier.clickable { onDateChange(Date()) }
                        )
                    }
                }
                Text(
                    text = "›",
                    fontSize = 20.sp,
                    color = if (isToday) Color.White.copy(alpha = 0.1f) else TextTertiary,
                    modifier = Modifier
                        .clickable(enabled = !isToday) {
                            val cal = Calendar.getInstance().apply { time = selectedDate; add(Calendar.DAY_OF_YEAR, 1) }
                            if (cal.time <= Date()) onDateChange(cal.time)
                        }
                        .padding(16.dp)
                )
            }
        }

        // Time slot mood selectors
        items(TIME_SLOTS) { slot ->
            val loggedMoodId = todayLogs[slot.id]
            val loggedMood = loggedMoodId?.let { id -> TRACKER_MOODS.find { it.id == id } }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = slot.icon, fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = slot.label,
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = slot.hours,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        if (loggedMood != null) {
                            Text(
                                text = "${loggedMood.emoji} ${loggedMood.label}",
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 11.sp,
                                color = loggedMood.color
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TRACKER_MOODS.forEach { tm ->
                            val selected = loggedMoodId == tm.id
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (selected) CardBgHover else Color.White.copy(alpha = 0.02f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (selected) tm.color.copy(alpha = 0.4f) else CardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onLogMood(slot.id, tm.id) }
                                    .padding(vertical = 10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = tm.emoji, fontSize = 22.sp)
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = tm.label,
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 9.sp,
                                        color = if (selected) tm.color else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Week chart
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
                        text = "THIS WEEK",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth().height(80.dp)
                    ) {
                        weekData.forEach { (day, avg, date) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            ) {
                                if (avg != null) {
                                    val height = (avg / 5f * 48).dp
                                    val info = scoreToMoodInfo(avg)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(height)
                                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp))
                                            .background(info.third.copy(alpha = 0.3f))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(2.dp))
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = day,
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 9.sp,
                                    color = if (dateKey(date) == todayKey) Accent else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionsTab(
    selectedMoodId: String?,
    mood: Mood?,
    onSelectMood: (String?) -> Unit,
    onStartSession: (MoodSession) -> Unit
) {
    if (selectedMoodId == null) {
        // Mood selection grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(MOODS) { m ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(m.bg, RoundedCornerShape(16.dp))
                        .border(1.dp, m.border, RoundedCornerShape(16.dp))
                        .clickable { onSelectMood(m.id) }
                        .padding(vertical = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = m.emoji, fontSize = 32.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = m.name,
                            fontFamily = FontFamily.Serif,
                            fontSize = 18.sp,
                            color = m.color
                        )
                    }
                }
            }
        }
    } else if (mood != null) {
        // Selected mood - show sessions
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = "← All moods",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    color = TextTertiary,
                    modifier = Modifier.clickable { onSelectMood(null) }
                )
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text(text = mood.emoji, fontSize = 40.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Feeling ${mood.name}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Light,
                        color = mood.color
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = mood.message,
                        fontFamily = FontFamily.Serif,
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.widthIn(max = 360.dp)
                    )
                }
            }

            items(mood.sessions) { session ->
                val meta = LEN_META[session.length]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .clickable { onStartSession(session) }
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .background(mood.bg, CircleShape)
                                .border(1.dp, mood.border, CircleShape)
                        ) {
                            Text(text = meta?.icon ?: "", fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = session.label,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 18.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${session.mins} min",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Text(
                                text = (meta?.label ?: "").uppercase(),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 10.sp,
                                color = mood.color.copy(alpha = 0.7f),
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = session.steps.first(),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = TextTertiary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InsightsTab(
    insights: InsightData,
    onAction: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Stat cards
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    Triple("Weekly Avg", insights.weekAvg, true),
                    Triple("Monthly Avg", insights.monthAvg, true)
                ).forEach { (label, value, isMood) ->
                    val info = scoreToMoodInfo(value)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(CardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label.uppercase(),
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 9.sp,
                                color = TextMuted,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(text = info.first, fontSize = 24.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = info.second,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 12.sp,
                                color = info.third
                            )
                            if (value != null) {
                                Text(
                                    text = "${"%.1f".format(value)}/5",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("STREAK", fontFamily = FontFamily.SansSerif, fontSize = 9.sp, color = TextMuted, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = if (insights.streak > 0) "${insights.streak}d" else "Start",
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            color = TextPrimary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CHECK-INS", fontFamily = FontFamily.SansSerif, fontSize = 9.sp, color = TextMuted, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "${insights.totalLogs}",
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Light,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Trend
        if (insights.trend != null) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (insights.trend) {
                                "improving" -> "\uD83D\uDCC8"
                                "declining" -> "\uD83D\uDCC9"
                                else -> "➡\uFE0F"
                            },
                            fontSize = 24.sp
                        )
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("TREND", fontFamily = FontFamily.SansSerif, fontSize = 10.sp, color = TextMuted, letterSpacing = 1.5.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = when (insights.trend) {
                                    "improving" -> "Mood improving"
                                    "declining" -> "Mood dipping"
                                    else -> "Mood steady"
                                },
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Light,
                                color = when (insights.trend) {
                                    "improving" -> Color(150, 200, 150, 230)
                                    "declining" -> Color(210, 150, 150, 230)
                                    else -> Color(200, 190, 130, 230)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Recommendations
        if (insights.recommendations.isNotEmpty()) {
            item {
                Text(
                    text = "RECOMMENDATIONS",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 9.sp,
                    color = TextMuted,
                    letterSpacing = 1.5.sp
                )
            }
            items(insights.recommendations) { rec ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(14.dp))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = rec.icon, fontSize = 18.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = rec.text,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 20.sp
                            )
                            if (rec.action != null) {
                                Spacer(Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .background(AccentBg, RoundedCornerShape(100.dp))
                                        .border(1.dp, AccentBorder, RoundedCornerShape(100.dp))
                                        .clickable { onAction(rec.action) }
                                        .padding(horizontal = 16.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Go →",
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 11.sp,
                                        color = Accent
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
