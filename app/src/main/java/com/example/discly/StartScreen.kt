package com.example.discly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun StartScreen(
    onStartGame: (
        players: List<String>,
        holes: Int,
        courseName: String
    ) -> Unit
) {

    var courseName by remember {
        mutableStateOf("")
    }

    var players by remember {
        mutableStateOf(mutableListOf(""))
    }

    var holeCountText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "New Game",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Text("Course Name", color = Color.White)
        OutlinedTextField(

            value = courseName,

            onValueChange = {
                courseName = it
            },

            placeholder = {
                Text("Unnamed")
            },


            singleLine = true,

            modifier = Modifier.fillMaxWidth()
        )

        // --- RATOJEN MÄÄRÄ ---
        Text("Number Of Holes", color = Color.White)
        OutlinedTextField(
            value = holeCountText,

            onValueChange = {
                holeCountText = it.filter { c -> c.isDigit() }
            },

            placeholder = {
                Text("18")
            },

            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),

            singleLine = true,

            modifier = Modifier.fillMaxWidth()
        )

        // --- PELAAJAT ---
        Text("Players", color = Color.White)

        players.forEachIndexed { index, name ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { newName ->
                        players = players.toMutableList().also {
                            it[index] = newName
                        }
                    },

                    // 🔥 TÄRKEIN MUUTOS
                    placeholder = {
                        Text("Player ${index + 1}")
                    },

                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                if (players.size > 1) {
                    Button(
                        onClick = {
                            players = players.toMutableList().also {
                                it.removeAt(index)
                            }
                        }
                    ) {
                        Text("Delete")
                    }
                }
            }
        }

        Button(
            onClick = {
                // 🔥 LISÄÄ TYHJÄ KENTTÄ
                players = players.toMutableList().also {
                    it.add("")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Player")
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- START ---
        Button(
            onClick = {
                val holes = holeCountText.toIntOrNull()?.coerceAtLeast(1) ?: 18

                val finalPlayers = players.mapIndexed { index, name ->
                    if (name.isBlank()) {
                        "Player ${index + 1}"
                    } else {
                        name
                    }
                }

                onStartGame(
                    finalPlayers,
                    holes,

                    if (courseName.isBlank()) {
                        "Unnamed Course"
                    } else {
                        courseName
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start Game")
        }
    }
}