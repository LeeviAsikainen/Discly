package com.example.discly

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryScreen(
    players: List<String>,
    scoreTable: List<List<Int>>,
    pars: List<Int>,
    colors: AppColors,
    courseName: String,
    onBack: () -> Unit,
    onMenu: () -> Unit
) {

    var dragX by remember {
        mutableStateOf(0f)
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

                        // 👉 swipe takaisin peliin
                        if (dragX > 120f) {
                            onBack()
                        }

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Round Result",
                    color = colors.text,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RESULT CARD
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(colors.card)
                    .padding(20.dp)
            ) {

                val scores = scoreTable[0]

                val totalDiff = scores.indices
                    .filter { scores[it] > 0 }
                    .sumOf { hole ->
                        scores[hole] - pars[hole]
                    }

                // TOP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = players[0],
                            color = colors.text,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${scores.size} Holes • $courseName",
                            color = colors.subText,
                            fontSize = 16.sp
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {

                        Text(
                            text = when {
                                totalDiff > 0 -> "+$totalDiff"
                                totalDiff == 0 -> "E"
                                else -> totalDiff.toString()
                            },

                            color = when {
                                totalDiff > 0 -> Color(0xFFFF5252)
                                totalDiff < 0 -> Color(0xFF4CAF50)
                                else -> colors.text
                            },

                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "TOTAL",
                            color = colors.subText,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    color = colors.accent.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ONLY GRID SCROLLS
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),

                    verticalArrangement = Arrangement.spacedBy(10.dp),

                    horizontalArrangement = Arrangement.spacedBy(10.dp),

                    modifier = Modifier.fillMaxSize()
                ) {

                    itemsIndexed(scores) { index, score ->

                        val diff =
                            if (score == 0) null
                            else score - pars[index]

                        val display = when {
                            diff == null -> "-"
                            diff > 0 -> "+$diff"
                            diff == 0 -> "E"
                            else -> diff.toString()
                        }

                        val bgColor = when {
                            diff == null -> colors.background
                            diff > 0 -> Color(0x33FF5252)
                            diff < 0 -> Color(0x334CAF50)
                            else -> colors.background
                        }

                        val textColor = when {
                            diff == null -> colors.subText
                            diff > 0 -> Color(0xFFFF5252)
                            diff < 0 -> Color(0xFF4CAF50)
                            else -> colors.text
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "${index + 1}",
                                color = colors.accent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .height(64.dp)
                                    .width(52.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(bgColor),

                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = display,
                                    color = textColor,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // FIXED BUTTON
            Button(
                onClick = onMenu,

                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent
                ),

                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
            ) {

                Text(
                    text = "Back to menu",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
