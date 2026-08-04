package com.example.discly

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
                    text = "Round Result",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colors.text
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

                // Lasketaan diffit (vain pelatut väylät)
                val diffs = scores.indices
                    .filter { scores[it] > 0 }
                    .map { scores[it] - pars[it] }

                val diffsWithIndex = scores.indices
                    .filter { scores[it] > 0 }
                    .map { index ->
                        index to (scores[index] - pars[index])
                    }

                val bestEntry = diffsWithIndex.minByOrNull { it.second }
                val worstEntry = diffsWithIndex.maxByOrNull { it.second }

                val best = bestEntry?.second
                val bestHole = bestEntry?.first

                val worst = worstEntry?.second
                val worstHole = worstEntry?.first

                val average: Double? =
                    if (diffsWithIndex.isNotEmpty())
                        diffsWithIndex.map { it.second }.average()
                    else null


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
                            text = courseName,
                            color = colors.text,
                            fontSize = 30.sp,
                            //fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${scores.size} Holes",
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

                            fontSize = 42.sp,
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

// TOP DIVIDER
                HorizontalDivider(
                    color = colors.accent.copy(alpha = 0.4f)
                )

                Spacer(modifier = Modifier.height(16.dp))

// STATS ROW (BEST + AVG)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "BEST",
                            color = colors.subText,
                            fontSize = 12.sp
                        )

                        Text(
                            text = best?.let {
                                val hole = bestHole?.plus(1)
                                val score = when {
                                    it > 0 -> "+$it"
                                    it == 0 -> "E"
                                    else -> it.toString()
                                }
                                "$score (H$hole)"
                            } ?: "-",

                            color = when {
                                best == null -> colors.subText
                                best > 0 -> Color(0xFFFF5252)
                                best < 0 -> Color(0xFF4CAF50)
                                else -> colors.text
                            },

                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "AVG",
                            color = colors.subText,
                            fontSize = 12.sp
                        )

                        Text(
                            text = average?.let {
                                val rounded = String.format("%.1f", it)
                                if (it > 0) "+$rounded"
                                else if (it == 0.0) "E"
                                else rounded
                            } ?: "-",

                            color = when {
                                average == null -> colors.subText
                                average > 0 -> Color(0xFFFF5252)
                                average < 0 -> Color(0xFF4CAF50)
                                else -> colors.text
                            },

                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "WORST",
                            color = colors.subText,
                            fontSize = 12.sp
                        )

                        Text(
                            text = worst?.let {
                                val hole = worstHole?.plus(1)
                                val score = when {
                                    it > 0 -> "+$it"
                                    it == 0 -> "E"
                                    else -> it.toString()
                                }
                                "$score (H$hole)"
                            } ?: "-",

                            color = when {
                                worst == null -> colors.subText
                                worst > 0 -> Color(0xFFFF5252)
                                worst < 0 -> Color(0xFF4CAF50)
                                else -> colors.text
                            },

                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

// BOTTOM DIVIDER
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


                        val isBest = index == bestHole
                        val isWorst = index == worstHole


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
                                    .background(bgColor)
                                    .border(
                                        width = if (isBest || isWorst) 2.dp else 0.dp,
                                        color = when {
                                            isBest -> Color(0xFF4CAF50)   // vihreä
                                            isWorst -> Color(0xFFFF5252)  // punainen
                                            else -> Color.Transparent
                                        },
                                        shape = RoundedCornerShape(20.dp)
                                    ),

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
                    .height(58.dp)
            ) {

                Text(
                    text = "Back to menu",
                    color = Color.White,
                    fontSize = 16.sp,
                //    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
