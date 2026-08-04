package com.example.udisc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.combinedClickable

@Composable
fun GamesScreen(
    colors: AppColors,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    var games by remember {
        mutableStateOf<List<GameResult>>(emptyList())
    }

    var selectedGame by remember { mutableStateOf<GameResult?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {

        games = AppStorage
            .loadHistory(context)
            .sortedByDescending { it.timestamp }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.accent
                )
            }


            Text(
                text = "Saved Games",
                style = MaterialTheme.typography.headlineMedium,
                color = colors.text
            )
        }



        if (games.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "No saved games",
                    color = colors.subText
                )
            }

        } else {


            LazyColumn(
                modifier = Modifier.weight(1f),

                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {


                items(games) { game ->


                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    // (voit myöhemmin lisätä: open game details)
                                },
                                onLongClick = {
                                    selectedGame = game
                                    showDeleteDialog = true
                                }
                            ),

                        colors = CardDefaults.cardColors(
                            containerColor = colors.card
                        )
                    ) {


                        Column(
                            modifier = Modifier
                                .padding(16.dp),

                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {


                            Row(
                                modifier = Modifier.fillMaxWidth(),

                                horizontalArrangement = Arrangement.SpaceBetween,

                                verticalAlignment = Alignment.CenterVertically
                            ) {


                                Text(
                                    text = game.courseName,

                                    color = colors.text,

                                    style = MaterialTheme.typography.titleMedium
                                )



                                val score =
                                    calculateScoreVsPar(game)



                                val scoreColor =
                                    when {

                                        score < 0 ->
                                            Color(0xFF4CAF50)

                                        score > 0 ->
                                            Color(0xFFF44336)

                                        else ->
                                            colors.text
                                    }



                                Text(
                                    text = formatScore(score),

                                    color = scoreColor,

                                    style = MaterialTheme.typography.titleLarge
                                )
                            }



                            Text(
                                text = formatTimestamp(game.timestamp),

                                color = colors.subText,

                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            if (showDeleteDialog && selectedGame != null) {

                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                        selectedGame = null
                    },

                    title = {
                        Text("Delete game?")
                    },

                    text = {
                        Text("Do you want to delete this saved game?")
                    },

                    confirmButton = {
                        TextButton(
                            onClick = {

                                selectedGame?.let { gameToDelete ->

                                    // 🔥 POISTA HISTORIASTA
                                    AppStorage.deleteGame(context, gameToDelete)

                                    // 🔄 PÄIVITÄ LISTA
                                    games = AppStorage
                                        .loadHistory(context)
                                        .sortedByDescending { it.timestamp }
                                }

                                showDeleteDialog = false
                                selectedGame = null
                            }
                        ) {
                            Text("Delete")
                        }
                    },

                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                selectedGame = null
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }


        Button(

            onClick = onBack,

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accent
            )
        ) {

            Text("Back")
        }
    }
}



fun calculateScoreVsPar(game: GameResult): Int {

    val totalScore =
        game.scores.firstOrNull()?.sum() ?: 0

    val totalPar =
        game.pars.sum()

    return totalScore - totalPar
}



fun formatScore(score: Int): String {

    return when {

        score > 0 ->
            "+$score"

        score < 0 ->
            "$score"

        else ->
            "E"
    }
}



fun formatTimestamp(timestamp: Long): String {

    val sdf =
        SimpleDateFormat(
            "d.M.yyyy HH:mm",
            Locale.getDefault()
        )

    return sdf.format(Date(timestamp))
}