package com.mindfulminutes.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class StatEntry(
    val id: String,
    val cat: String,
    val name: String,
    val mins: Int,
    val date: String
)

data class JournalEntry(
    val text: String,
    val exercise: String?,
    val date: String
)

data class InsightData(
    val weekAvg: Float?,
    val monthAvg: Float?,
    val trend: String?,
    val bestTime: String?,
    val worstTime: String?,
    val streak: Int,
    val totalLogs: Int,
    val recommendations: List<Recommendation>,
    val timeAvgs: Map<String, Float?>
)

data class Recommendation(
    val icon: String,
    val text: String,
    val action: String?
)

fun dateKey(date: Date = Date()): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return sdf.format(date)
}

fun dayName(date: Date): String {
    val sdf = SimpleDateFormat("EEE", Locale.US)
    return sdf.format(date)
}

fun monthName(date: Date): String {
    val sdf = SimpleDateFormat("MMM", Locale.US)
    return sdf.format(date)
}

fun scoreToMoodInfo(score: Float?): Triple<String, String, androidx.compose.ui.graphics.Color> {
    if (score == null) return Triple("—", "No data", androidx.compose.ui.graphics.Color(255, 255, 255, 51))
    return when {
        score >= 4.5f -> Triple("\uD83D\uDE0A", "Great", androidx.compose.ui.graphics.Color(150, 200, 150, 230))
        score >= 3.5f -> Triple("\uD83D\uDE42", "Good", androidx.compose.ui.graphics.Color(170, 195, 140, 230))
        score >= 2.5f -> Triple("\uD83D\uDE10", "Okay", androidx.compose.ui.graphics.Color(200, 190, 130, 230))
        score >= 1.5f -> Triple("\uD83D\uDE14", "Low", androidx.compose.ui.graphics.Color(190, 165, 140, 230))
        else -> Triple("\uD83D\uDE22", "Rough", androidx.compose.ui.graphics.Color(170, 150, 180, 230))
    }
}

fun generateInsights(logs: Map<String, Map<String, String>>): InsightData {
    val entries = logs.entries.sortedBy { it.key }
    if (entries.isEmpty()) return InsightData(null, null, null, null, null, 0, 0, emptyList(), emptyMap())

    val now = Calendar.getInstance()
    val weekAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
    val monthAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
    val twoWeeksAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -14) }

    val weekScores = mutableListOf<Int>()
    val monthScores = mutableListOf<Int>()
    val allScores = mutableListOf<Int>()
    val timeScores = mutableMapOf<String, MutableList<Int>>()
    TIME_SLOTS.forEach { timeScores[it.id] = mutableListOf() }

    val prevWeekScores = mutableListOf<Int>()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    entries.forEach { (key, slots) ->
        val date = Calendar.getInstance().apply { time = sdf.parse(key) ?: Date() }
        slots.forEach { (slot, moodId) ->
            val mood = TRACKER_MOODS.find { it.id == moodId } ?: return@forEach
            allScores.add(mood.score)
            if (date >= weekAgo) weekScores.add(mood.score)
            if (date >= monthAgo) monthScores.add(mood.score)
            if (date >= twoWeeksAgo && date < weekAgo) prevWeekScores.add(mood.score)
            timeScores[slot]?.add(mood.score)
        }
    }

    fun avg(list: List<Int>): Float? = if (list.isEmpty()) null else list.sum().toFloat() / list.size

    val weekAvg = avg(weekScores)
    val monthAvg = avg(monthScores)
    val prevAvg = avg(prevWeekScores)

    val trend = if (weekAvg != null && prevAvg != null) {
        val diff = weekAvg - prevAvg
        when {
            diff > 0.3f -> "improving"
            diff < -0.3f -> "declining"
            else -> "stable"
        }
    } else null

    val timeAvgs = timeScores.mapValues { avg(it.value) }
    val validTimeAvgs = timeAvgs.filter { it.value != null }
    val bestTime = validTimeAvgs.maxByOrNull { it.value!! }?.key
    val worstTime = validTimeAvgs.minByOrNull { it.value!! }?.key

    var streak = 0
    for (i in 0 until 365) {
        val d = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
        val key = dateKey(d.time)
        if (logs[key]?.isNotEmpty() == true) streak++ else break
    }

    val recs = mutableListOf<Recommendation>()
    if (weekAvg != null) {
        when {
            weekAvg < 2.5f -> {
                recs.add(Recommendation("\uD83D\uDC9A", "Tough week. Try a Deep Dive session.", "sessions"))
                recs.add(Recommendation("\uD83C\uDF3F", "Connecting with someone can help.", null))
            }
            weekAvg < 3.5f -> recs.add(Recommendation("\uD83C\uDF24\uFE0F", "Mixed week. Steady Calm sessions help.", "sessions"))
            else -> recs.add(Recommendation("✨", "Doing well! Keep momentum with daily practice.", "minutes"))
        }
    }
    when (worstTime) {
        "morning" -> recs.add(Recommendation("\uD83C\uDF05", "Mornings are hardest. Try breathing before phone.", null))
        "evening" -> recs.add(Recommendation("\uD83C\uDF19", "Evenings dip. Body scan before bed helps.", null))
    }
    when (trend) {
        "declining" -> recs.add(Recommendation("\uD83E\uDEC2", "Trending down. Be extra gentle.", null))
        "improving" -> recs.add(Recommendation("\uD83C\uDF31", "Trending up — keep doing what works!", null))
    }
    if (streak >= 7) recs.add(Recommendation("\uD83D\uDD25", "$streak-day streak! Consistency builds awareness.", null))

    return InsightData(weekAvg, monthAvg, trend, bestTime, worstTime, streak, allScores.size, recs, timeAvgs)
}
