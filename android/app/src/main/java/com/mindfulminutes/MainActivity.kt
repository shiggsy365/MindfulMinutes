package com.mindfulminutes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mindfulminutes.data.*
import com.mindfulminutes.ui.components.NavBar
import com.mindfulminutes.ui.components.SettingsPanel
import com.mindfulminutes.ui.screens.*
import com.mindfulminutes.ui.theme.Background
import com.mindfulminutes.ui.theme.MindfulMinutesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindfulMinutesTheme {
                MindfulMinutesApp()
            }
        }
    }
}

@Composable
fun MindfulMinutesApp() {
    var page by remember { mutableStateOf("zen") }
    var unsplashKey by remember { mutableStateOf("") }
    var settingsOpen by remember { mutableStateOf(false) }
    var intention by remember { mutableStateOf("") }
    var favourites by remember { mutableStateOf(listOf<String>()) }
    var stats by remember { mutableStateOf(listOf<StatEntry>()) }
    var journal by remember { mutableStateOf(listOf<JournalEntry>()) }
    var notifications by remember { mutableStateOf(mapOf("morning" to false, "afternoon" to false, "evening" to false)) }

    val toggleFav: (String) -> Unit = { id ->
        favourites = if (id in favourites) favourites - id else favourites + id
    }

    val addStats: (Exercise) -> Unit = { exercise ->
        stats = stats + StatEntry(
            id = exercise.id,
            cat = exercise.cat,
            name = exercise.name,
            mins = exercise.mins,
            date = dateKey()
        )
    }

    val addJournal: (String, String?) -> Unit = { text, exerciseName ->
        journal = journal + JournalEntry(
            text = text,
            exercise = exerciseName,
            date = dateKey()
        )
    }

    // Settings panel
    SettingsPanel(
        isOpen = settingsOpen,
        onDismiss = { settingsOpen = false },
        apiKey = unsplashKey,
        onSaveKey = { unsplashKey = it },
        notifications = notifications,
        onToggleNotification = { key ->
            notifications = notifications.toMutableMap().apply {
                this[key] = !(this[key] ?: false)
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Main content area with bottom padding for nav bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) togetherWith
                            fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                },
                label = "pageTransition"
            ) { currentPage ->
                when (currentPage) {
                    "zen" -> ZenScreen(
                        intention = intention,
                        onSetIntention = { intention = it }
                    )
                    "minutes" -> MinutesScreen(
                        favourites = favourites,
                        toggleFav = toggleFav,
                        stats = stats,
                        addStats = addStats,
                        journal = journal,
                        addJournal = addJournal
                    )
                    "mood" -> MoodBoardScreen(
                        navigateTo = { page = it }
                    )
                    "escapes" -> GuidedEscapesScreen()
                    "practice" -> StatsScreen(
                        stats = stats,
                        journal = journal,
                        favourites = favourites
                    )
                    else -> ZenScreen(
                        intention = intention,
                        onSetIntention = { intention = it }
                    )
                }
            }
        }

        // Bottom navigation
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            NavBar(
                activePage = page,
                onPageChange = { page = it },
                onSettingsClick = { settingsOpen = true }
            )
        }
    }
}
