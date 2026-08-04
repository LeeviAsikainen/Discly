package com.example.discly

import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight


@Composable
fun GameScreen(
    players: List<String>,
    totalHoles: Int,
    courseName: String,
    pars: List<Int>,
    savedScores: List<List<Int>>? = null,
    startHole: Int = 0,
    colors: AppColors,
    onGameFinished: () -> Unit,
    onExitToMenu: () -> Unit,
    startTime: Long = System.currentTimeMillis(),
) {


    var showExitDialog by remember {
        mutableStateOf(false)
    }


    BackHandler {

        showExitDialog = true
    }

    var showDragHint by remember { mutableStateOf(true) }

    var showSummary by remember {
        mutableStateOf(false)
    }

    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    val scores = listOf(5, 4, 3, 2, 1, 0) // -1 = "Muu"

    //muulle
    var customScoreText by remember { mutableStateOf("") }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customInput by remember { mutableStateOf("") }

    // --- STATE ---
    var currentHoleIndex by remember {
        mutableStateOf(startHole)
    }

    var currentPlayerIndex by remember { mutableStateOf(0) }

    var scoreTable by remember {

        mutableStateOf(

            savedScores?.map {
                it.toMutableList()
            }
                ?: List(players.size) {
                    MutableList(totalHoles) {0}
                }

        )
    }

    val playerScores = scoreTable[currentPlayerIndex]



    val playedHoles = playerScores.indices.filter {
        playerScores[it] > 0
    }

    val totalDiff = playedHoles.sumOf { index ->
        playerScores[index] - pars[index]
    }



    val totalDisplay = when {
        playedHoles.isEmpty() -> "E"
        totalDiff > 0 -> "+$totalDiff"
        totalDiff == 0 -> "E"
        else -> totalDiff.toString()
    }
    val totalColor = when {
        totalDiff > 0 -> Color(0xFFFF5252)
        totalDiff < 0 -> Color(0xFF4CAF50)
        else -> colors.text
    }

    val courseHistory = remember {
        AppStorage.loadResults(context)
    }

    val holeStats = remember(
        courseHistory,
        currentHoleIndex,
        courseName
    ) {

        val scores = courseHistory
            .filter { it.courseName == courseName }
            .mapNotNull { game ->
                game.scores
                    .flatMap { it }
                    .getOrNull(currentHoleIndex)
                    ?.takeIf { it > 0 }
            }

        if (scores.isEmpty()) {
            Pair("-", "-")
        } else {

            val best = scores.minOrNull()

            val average = scores.average()

            Pair(
                best.toString(),
                String.format("%.1f", average)
            )
        }
    }


    // --- LAYOUT ---
    var containerHeight by remember { mutableStateOf(1f) }
    var containerWidth by remember { mutableStateOf(1f) }

    val circleSizeDp = 100.dp
    val circleSizePx = with(density) { circleSizeDp.toPx() }

    var dragY by remember { mutableStateOf(0f) }
    var idleY by remember { mutableStateOf(0f) }

    var isDragging by remember { mutableStateOf(false) }
    var isSelecting by remember { mutableStateOf(false) }

    var currentScore by remember { mutableStateOf(3) }
    var lastScore by remember { mutableStateOf(3) }

    val offsetX = remember { Animatable(0f) }

    val animatedY by animateFloatAsState(
        targetValue = dragY,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "smoothY"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val ballAlpha = remember { Animatable(1f) }
    val ballScale = remember { Animatable(1f) }
    val spawnOffsetX = remember { Animatable(0f) }

    if (showSummary) {

        SummaryScreen(
            players = players,
            scoreTable = scoreTable,
            pars = pars,
            courseName = courseName,

            colors = colors,

            onBack = {
                showSummary = false
            },
            onMenu = {
                onExitToMenu()
            }
        )

        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .onSizeChanged {
                containerHeight = it.height.toFloat()
                containerWidth = it.width.toFloat()
                idleY = containerHeight * 0.75f
                dragY = idleY
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val threshold = containerWidth * 0.10f //.25f

                        scope.launch {
                            when {
                                offsetX.value > threshold && currentHoleIndex > 0 -> {
                                    offsetX.animateTo(containerWidth, tween(150))
                                    currentHoleIndex--

                                    triggerBallFX(
                                        scope = scope,
                                        fromRight = false, // 🔥 tuli vasemmalta
                                        width = containerWidth,
                                        alpha = ballAlpha,
                                        scale = ballScale,
                                        offsetX = spawnOffsetX,
                                        //haptic = haptic
                                    )
                                }

                                offsetX.value < -threshold -> {
                                    offsetX.animateTo(-containerWidth, tween(150))
                                    if (currentHoleIndex == totalHoles - 1) {


                                        val duration =
                                            (System.currentTimeMillis() - startTime) / 1000


                                        AppStorage.saveGameResult(
                                            context,

                                            GameResult(
                                                courseName = courseName,
                                                players = players,
                                                scores = scoreTable.map {
                                                    it.toList()
                                                },
                                                pars = pars,
                                                durationSec = duration
                                            )
                                        )


                                        AppStorage.deleteCurrentGame(context)

                                        onGameFinished()

                                        showSummary = true
                                    } else {
                                        currentHoleIndex++
                                        AppStorage.saveCurrentGame(
                                            context,

                                            SavedGame(
                                                courseName = courseName,
                                                players = players,
                                                scores = scoreTable.map {
                                                    it.toList()
                                                },
                                                pars = pars,
                                                totalHoles = totalHoles,
                                                currentHole = currentHoleIndex,
                                                startTime = startTime
                                            )
                                        )
                                    }

                                    triggerBallFX(
                                        scope = scope,
                                        fromRight = true, // 🔥 tuli oikealta
                                        width = containerWidth,
                                        alpha = ballAlpha,
                                        scale = ballScale,
                                        offsetX = spawnOffsetX,
                                        //haptic = haptic
                                    )
                                }
                            }
                            offsetX.snapTo(0f)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        showDragHint = false
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                    }
                )
            }
    ) {

        // 🔹 HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {

            // Par vasen
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp),

                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "PAR",
                    color = colors.subText,
                    fontSize = 12.sp
                )

                Text(
                    pars[currentHoleIndex].toString(),
                    color = colors.text,
                    fontSize = 34.sp
                )
            }

            // 🔹 HOLE keskellä oikeasti
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = courseName,
                    color = colors.subText,
                    fontSize = 12.sp
                )

                Text(
                    "${currentHoleIndex + 1} / $totalHoles",
                    color = colors.text,
                    fontSize = 34.sp
                )
            }

            // 🔹 TOTAL oikealla
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp),

                horizontalAlignment = Alignment.End
            ) {

                Text(
                    "TOTAL",
                    color = colors.subText,
                    fontSize = 12.sp
                )

                Text(
                    text = totalDisplay,
                    color = totalColor,
                    fontSize = 34.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.width(90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "BEST",
                        color = colors.text.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )

                    Text(
                        text = holeStats.first,
                        color = colors.text.copy(alpha = 0.4f),
                        fontSize = 24.sp,
                        //fontWeight = FontWeight.Bold
                    )
                }


                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(
                            colors.subText.copy(alpha = 0.4f)
                        )
                )


                Column(
                    modifier = Modifier.width(90.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "AVERAGE",
                        color = colors.text.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )

                    Text(
                        text = holeStats.second,
                        color = colors.text.copy(alpha = 0.4f),
                        fontSize = 24.sp,
                        //fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- SCORE LIST ---
        AnimatedVisibility(isSelecting, enter = fadeIn(), exit = fadeOut()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                scores.forEach { score ->

                    if (score == 0) {

                        // 🔥 MUU NAPPI
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(getWeight(0))
                                .background(colors.card)
                        ) {
                            Text(
                                "Muu",
                                color = colors.text,
                                modifier = Modifier.padding(24.dp)
                            )
                        }

                    } else {

                        val isActive = score == currentScore

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(getWeight(score))
                                .background(
                                    if (isActive) colors.accent
                                    else Color(0xFF1E1E1E)
                                )
                        ) {
                            Text(
                                "$score",
                                color = colors.text,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- PALLO ---
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (offsetX.value + spawnOffsetX.value).roundToInt(),
                        (animatedY - circleSizePx / 2 - 300f).roundToInt()
                    )
                }
                .size(circleSizeDp)
                .graphicsLayer {
                    alpha = ballAlpha.value
                    scaleX = ballScale.value
                    scaleY = ballScale.value
                }
                .clip(CircleShape)
                .background(colors.ball)
                .align(Alignment.TopCenter)
                .pointerInput(containerHeight) {
                    detectDragGestures(

                        onDragStart = {
                            showDragHint = false
                            isDragging = true
                            isSelecting = true
                        },

                        onDragEnd = {
                            isDragging = false
                            isSelecting = false
                            dragY = idleY

                            // 🔥 JOS "Muu" VALITTU → avaa dialogi
                            if (currentScore == 0) {
                                showCustomDialog = true
                                return@detectDragGestures
                            }

                            // seuraava pelaaja
                            currentPlayerIndex =
                                (currentPlayerIndex + 1) % players.size
                        },

                        onDrag = { change, dragAmount ->
                            change.consume()

                            dragY = (dragY + dragAmount.y)
                                .coerceIn(0f, containerHeight)

                            val score = calculateScoreFromY(dragY, containerHeight)

                            if (score != lastScore) {
                                haptic.performHapticFeedback(
                                    HapticFeedbackType.TextHandleMove
                                )
                                lastScore = score
                            }

                            if (score != 0) {
                                currentScore = score
                            } else {
                                currentScore = 0
                            }

                            // tallenna
                            scoreTable = scoreTable.toMutableList().also { table ->
                                table[currentPlayerIndex] =
                                    table[currentPlayerIndex].toMutableList().also {
                                        it[currentHoleIndex] = score
                                    }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = scoreTable[currentPlayerIndex][currentHoleIndex].toString(),
                color = Color.Black,
                fontSize = 42.sp
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            AnimatedVisibility(
                visible = showDragHint,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {

                Column(

                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                        .offset(y = arrowOffset.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = colors.subText,
                        modifier = Modifier.size(42.dp)
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = colors.subText,
                        modifier = Modifier
                            .size(42.dp)
                            .offset(y = (-18).dp)
                    )

                    //Spacer(modifier = Modifier.height(110.dp))

                    Text(
                        text = "Drag to set score",
                        color = colors.subText,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
    if (showCustomDialog) {

        AlertDialog(
            onDismissRequest = {
                showCustomDialog = false
            },

            containerColor = colors.card,

            confirmButton = {
                TextButton(
                    onClick = {
                        val value = customInput.toIntOrNull() ?: 0

                        // 🔥 tallenna score
                        scoreTable = scoreTable.toMutableList().also { table ->
                            table[currentPlayerIndex] =
                                table[currentPlayerIndex].toMutableList().also {
                                    it[currentHoleIndex] = value
                                }
                        }

                        showCustomDialog = false
                        customInput = ""
                    }
                ) {
                    Text(
                        "Confirm",
                        color = colors.accent
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCustomDialog = false
                    }
                ) {
                    Text(
                        "Cancel",
                        color = colors.accent
                    )
                }
            },
            title = {
                Text(
                    "Enter score",
                    color = colors.text
                )
            },
            text = {
                OutlinedTextField(
                    textStyle = TextStyle(
                        color = colors.text
                    ),
                    value = customInput,
                    onValueChange = {
                        customInput = it.filter { c -> c.isDigit() }
                    },

                    placeholder = {
                        Text(
                            "0",
                            color = colors.subText
                        )
                    },

                    singleLine = true,

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        )
    }

    if (showExitDialog) {

        AlertDialog(

            onDismissRequest = {
                showExitDialog = false
            },


            title = {
                Text(
                    "Exit Round?"
                )
            },


            text = {
                Text(
                    "The round will be saved as an unfinished game."
                )
            },


            confirmButton = {

                TextButton(

                    onClick = {

                        AppStorage.saveCurrentGame(
                            context,

                            SavedGame(
                                courseName = courseName,
                                players = players,
                                scores = scoreTable.map {
                                    it.toList()
                                },
                                pars = pars,
                                totalHoles = totalHoles,
                                currentHole = currentHoleIndex,
                                startTime = startTime
                            )
                        )


                        showExitDialog = false

                        onExitToMenu()
                    }

                ) {

                    Text("Exit")
                }
            },


            dismissButton = {

                TextButton(

                    onClick = {

                        showExitDialog = false
                    }

                ) {

                    Text("Cancel")
                }
            }
        )
    }
}
fun getWeight(score: Int): Float {
    return when (score) {

        5 -> 2f
        4 -> 2.25f
        3 -> 2.5f
        2 -> 1.5f
        1 -> 1f
        0 -> 1.5f

        else -> 1.2f
    }
}

fun calculateScoreFromY(
    y: Float,
    totalHeight: Float
): Int {

    val scores = listOf(5, 4, 3, 2, 1)

    val totalWeight = scores.sumOf {
        getWeight(it).toDouble()
    }.toFloat()

    var currentY = 0f

    scores.forEach { score ->

        val sectionHeight =
            totalHeight * (getWeight(score) / totalWeight)

        if (y < currentY + sectionHeight) {
            return score
        }

        currentY += sectionHeight
    }

    return 0
}

fun triggerBallFX(
    scope: CoroutineScope,
    fromRight: Boolean,
    width: Float,
    alpha: Animatable<Float, AnimationVector1D>,
    scale: Animatable<Float, AnimationVector1D>,
    offsetX: Animatable<Float, AnimationVector1D>,
    //haptic: HapticFeedback
) {
    scope.launch {

        val startX = if (fromRight) width else -width

        // reset lähtötila
        offsetX.snapTo(startX)
        alpha.snapTo(0f)
        scale.snapTo(0.7f)

        // 1) slide + fade sisään
        launch {
            offsetX.animateTo(
                0f,
                tween(durationMillis = 220)
            )
        }

        launch {
            alpha.animateTo(1f, tween(180))
        }

        // 2) bounce (scale)
        scale.animateTo(
            1.1f,
            spring(dampingRatio = 0.5f, stiffness = 400f)
        )

        scale.animateTo(
            1f,
            spring(dampingRatio = 0.7f, stiffness = 600f)
        )

        // 3) haptic “impact”
        //haptic.performHapticFeedback(
        //    HapticFeedbackType.TextHandleMove
    }
}
