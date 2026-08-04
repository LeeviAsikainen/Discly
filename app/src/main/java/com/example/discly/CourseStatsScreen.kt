package com.example.discly

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.*

import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.discly.domain.computeCourseStats
import com.example.discly.ui.components.HoleStatRow


@Composable
fun CourseStatsScreen(
    rounds: List<GameResult>,
    courseName: String,
    colors: AppColors,
    onBack: () -> Unit,
    onStartGame: () -> Unit
) {

    var dragX by remember { mutableStateOf(0f) }

    val stats = remember(rounds, courseName) {
        computeCourseStats(rounds, courseName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (dragX > 120f) onBack()
                        dragX = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x
                    }
                )
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.accent
                    )
                }

                Text(
                    text = "Course Stats",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.text
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CARD
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.card)
                    .padding(20.dp)
            ) {

                // COURSE NAME
                Text(
                    text = courseName,
                    color = colors.text,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )


                HorizontalDivider(
                    color = colors.accent.copy(alpha = 0.4f)
                )


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


// GENERAL STATS

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    StatItem(
                        title = "Times Played",
                        value = stats.totalRounds.toString(),
                        colors = colors
                    )


                    StatItem(
                        title = "Avg Score",
                        value = String.format("%.1f", stats.averageScore),
                        colors = colors
                    )
                }


                Spacer(
                    modifier = Modifier.height(20.dp)
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    StatItem(
                        title = "Toughest Holes",
                        value =
                            if (stats.toughestHoles.isNotEmpty())
                                stats.toughestHoles.joinToString(", ")
                            else
                                "—",
                        colors = colors
                    )


                    StatItem(
                        title = "Avg Time",
                        value =
                            if(stats.averageDurationSec != null) {

                                val minutes =
                                    stats.averageDurationSec / 60

                                String.format(
                                    "%.0f min",
                                    minutes
                                )

                            } else {
                                "—"
                            },
                        colors = colors
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(color = colors.accent.copy(alpha = 0.4f))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        "Hole",
                        color = colors.subText
                    )

                    Text(
                        "Best",
                        color = colors.subText
                    )

                    Text(
                        "Average",
                        color = colors.subText
                    )

                    Text(
                        "Worst",
                        color = colors.subText
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                // LISTA
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    items(stats.holes) { hole ->

                        HoleStatRow(hole, colors)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // START BUTTON
            Button(
                onClick = onStartGame,
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Start Game", color = Color.White)
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    colors: AppColors
) {

    Column {

        Text(
            text = title,
            color = colors.subText,
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = colors.text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}