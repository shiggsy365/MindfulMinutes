package com.mindfulminutes.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mindfulminutes.data.*
import com.mindfulminutes.ui.components.ExerciseTimer
import com.mindfulminutes.ui.components.JournalOverlay
import com.mindfulminutes.ui.theme.*

@Composable
fun MinutesScreen(
    favourites: List<String>,
    toggleFav: (String) -> Unit,
    stats: List<StatEntry>,
    addStats: (Exercise) -> Unit,
    journal: List<JournalEntry>,
    addJournal: (String, String?) -> Unit
) {
    var selectedCat by remember { mutableStateOf<String?>(null) }
    var activeExercise by remember { mutableStateOf<Exercise?>(null) }
    var showJournal by remember { mutableStateOf(false) }
    var showFavs by remember { mutableStateOf(false) }

    val filtered = if (selectedCat != null) EXERCISES.filter { it.cat == selectedCat } else EXERCISES
    val display = when {
        showFavs -> EXERCISES.filter { it.id in favourites }
        selectedCat != null -> filtered
        else -> filtered.take(12)
    }

    // Exercise Timer overlay
    if (activeExercise != null) {
        ExerciseTimer(
            exercise = activeExercise!!,
            onClose = {
                activeExercise = null
                showJournal = false
            },
            isFav = activeExercise!!.id in favourites,
            onFav = { toggleFav(activeExercise!!.id) },
            onComplete = { addStats(activeExercise!!) },
            onJournal = { showJournal = true }
        )
    }

    // Journal overlay
    if (showJournal) {
        JournalOverlay(
            exerciseName = activeExercise?.name,
            onClose = { showJournal = false },
            onSave = { text -> addJournal(text, activeExercise?.name) }
        )
    }

    // Main content
    if (activeExercise == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0E100F), Background)
                    )
                )
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "60 EXERCISES · 6 CATEGORIES",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 10.sp,
                    color = TextMuted,
                    letterSpacing = 2.5.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Minutes of Mindfulness",
                    fontFamily = FontFamily.Serif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))

                // Surprise me button
                Box(
                    modifier = Modifier
                        .background(AccentBg, RoundedCornerShape(100.dp))
                        .border(1.dp, AccentBorder, RoundedCornerShape(100.dp))
                        .clickable {
                            activeExercise = EXERCISES.random()
                            selectedCat = null
                            showFavs = false
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "\uD83C\uDFB2 Surprise me",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        color = Accent.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Category filters - using a simple Row with wrapping
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // First row: All + Favourites
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        text = "All",
                        selected = selectedCat == null && !showFavs,
                        onClick = { selectedCat = null; showFavs = false }
                    )
                    Spacer(Modifier.width(6.dp))
                    if (favourites.isNotEmpty()) {
                        FilterChip(
                            text = "♥ Favourites",
                            selected = showFavs,
                            selectedColor = FavColor,
                            onClick = { showFavs = !showFavs; selectedCat = null }
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                // Second row: Categories
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CATEGORIES.take(3).forEach { cat ->
                        FilterChip(
                            text = "${cat.icon} ${cat.name}",
                            selected = selectedCat == cat.id,
                            selectedColor = cat.color,
                            selectedBg = cat.bg,
                            selectedBorder = cat.border,
                            onClick = {
                                selectedCat = if (selectedCat == cat.id) null else cat.id
                                showFavs = false
                            }
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CATEGORIES.drop(3).forEach { cat ->
                        FilterChip(
                            text = "${cat.icon} ${cat.name}",
                            selected = selectedCat == cat.id,
                            selectedColor = cat.color,
                            selectedBg = cat.bg,
                            selectedBorder = cat.border,
                            onClick = {
                                selectedCat = if (selectedCat == cat.id) null else cat.id
                                showFavs = false
                            }
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                }
            }

            // Category description
            if (selectedCat != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = CATEGORIES.find { it.id == selectedCat }?.desc ?: "",
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Exercise grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 260.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(display, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        isFav = exercise.id in favourites,
                        onClick = { activeExercise = exercise }
                    )
                }
            }

            if (selectedCat == null && !showFavs) {
                Text(
                    text = "Choose a category to explore all 60 exercises",
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 12.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    selectedColor: Color = TextPrimary,
    selectedBg: Color = CardBgHover,
    selectedBorder: Color = Color.White.copy(alpha = 0.15f),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (selected) selectedBg else CardBg,
                RoundedCornerShape(100.dp)
            )
            .border(
                1.dp,
                if (selected) selectedBorder else CardBorder,
                RoundedCornerShape(100.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.SansSerif,
            fontSize = 11.sp,
            color = if (selected) selectedColor else TextTertiary
        )
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    isFav: Boolean,
    onClick: () -> Unit
) {
    val cat = getCategoryForExercise(exercise)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(14.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
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
                Row {
                    if (isFav) {
                        Text(
                            text = "♥",
                            fontSize = 10.sp,
                            color = FavColor.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(
                        text = "${exercise.mins} min",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = exercise.name,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = TextPrimary.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = exercise.steps.first(),
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = TextTertiary,
                lineHeight = 18.sp,
                maxLines = 2
            )
        }
    }
}
