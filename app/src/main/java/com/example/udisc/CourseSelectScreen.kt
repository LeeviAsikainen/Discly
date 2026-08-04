package com.example.udisc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CourseSelectScreen(
    courses: List<Course>,
    colors: AppColors,

    unfinishedGame: SavedGame?,

    onContinueGame: () -> Unit,
    onCourseSelected: (Course) -> Unit,
    onNewCourse: (String) -> Unit,
    onSettings: () -> Unit,
    onViewGames: () -> Unit,
    onDeleteUnfinishedGame: () -> Unit
) {

    var searchQuery by remember { mutableStateOf("") }

    val filteredCourses = courses.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val exactMatch = courses.any {
        it.name.equals(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Discly",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.text
            )

            Row {
                IconButton(onClick = onViewGames) {
                    Icon(Icons.Default.BarChart, null, tint = colors.accent)
                }
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, null, tint = colors.accent)
                }
            }
        }

        // 🎮 UNFINISHED GAME (voi jäädä näkyviin)
        if (unfinishedGame != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.card
                )
            ) {
                Box(Modifier.fillMaxWidth()) {

                    Column(Modifier.padding(16.dp)) {
                        Text("Round in Progress", color = colors.text)
                        Text(unfinishedGame.courseName, color = colors.text)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onContinueGame,
                            colors = ButtonDefaults.buttonColors(colors.accent)
                        ) {
                            Text("Resume Round")
                        }
                    }

                    IconButton(
                        onClick = onDeleteUnfinishedGame,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, null, tint = colors.subText)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // 🔼 spacing kun tyhjä
            if (searchQuery.isEmpty()) {
                Spacer(modifier = Modifier.weight(0.3f))
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (searchQuery.isEmpty()) {
                Text(
                    text = "Select a course to start a round",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.subText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                )
            }

            // 🔍 YKSI JA SAMA TEXTFIELD
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search course") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔽 tulokset näkyy vain kun kirjoitetaan
            if (searchQuery.isNotEmpty()) {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {

                    items(filteredCourses) { course ->
                        Button(
                            onClick = { onCourseSelected(course) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(colors.accent)
                        ) {
                            Text(course.name)
                        }
                    }

                    if (!exactMatch) {
                        item {
                            Card(
                                onClick = { onNewCourse(searchQuery) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.accent.copy(alpha = 0.15f)
                                )
                            ) {
                                Text(
                                    text = "➕ Add \"$searchQuery\"",
                                    modifier = Modifier.padding(16.dp),
                                    color = colors.accent
                                )
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}