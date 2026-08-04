package com.example.discly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.text.font.FontWeight


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
    onDeleteUnfinishedGame: () -> Unit,
    onDeleteCourse: (Course) -> Unit
) {

    var selectedCourse by remember {
        mutableStateOf<Course?>(null)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var searchQuery by remember {
        mutableStateOf("")
    }


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

                IconButton(
                    onClick = onViewGames
                ) {

                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Games",
                        tint = colors.accent
                    )
                }


                IconButton(
                    onClick = onSettings
                ) {

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = colors.accent
                    )
                }
            }
        }



        // KESKEN GAME
        if (unfinishedGame != null) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),

                colors = CardDefaults.cardColors(
                    containerColor = colors.card
                )
            ) {


                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {


                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {


                        Text(
                            text = "Round in Progress",
                            color = colors.text
                        )


                        Text(
                            text = unfinishedGame.courseName,
                            color = colors.text
                        )


                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )


                        Button(
                            onClick = onContinueGame,

                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.accent
                            )
                        ) {

                            Text("Resume Round")
                        }
                    }



                    IconButton(
                        onClick = onDeleteUnfinishedGame,

                        modifier = Modifier.align(
                            Alignment.TopEnd
                        )
                    ) {

                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = colors.subText
                        )
                    }
                }
            }
        }



        Spacer(
            modifier = Modifier.height(20.dp)
        )



        if (searchQuery.isEmpty()) {

            Text(
                text = "Select a course to start a round",

                color = colors.subText,

                style = MaterialTheme.typography.bodyLarge
            )


            Spacer(
                modifier = Modifier.height(12.dp)
            )
        }



        OutlinedTextField(

            value = searchQuery,

            onValueChange = {
                searchQuery = it
            },

            placeholder = {
                Text("Search course")
            },

            singleLine = true,

            modifier = Modifier.fillMaxWidth()
        )



        Spacer(
            modifier = Modifier.height(16.dp)
        )



        // LIST + SCROLLBAR

        val listState = rememberLazyListState()

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp)
                    .padding(end = 8.dp)
            ) {

                items(filteredCourses) { course ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = {
                                    onCourseSelected(course)
                                },

                                onLongClick = {

                                    selectedCourse = course
                                    showDeleteDialog = true
                                }
                            ),

                        colors = CardDefaults.cardColors(
                            containerColor = colors.accent
                        )
                    ) {

                        Text(
                            text = course.name,
                            modifier = Modifier.padding(16.dp),
                            color = colors.text
                        )
                    }
                }


                // LISÄÄ UUSI RATA AINA LISTAN LOPPUUN
                if (
                    true
                ) {

                    item {

                        Card(
                            onClick = {
                                onNewCourse(searchQuery)
                            },

                            modifier = Modifier.fillMaxWidth(),

                            colors = CardDefaults.cardColors(
                                containerColor = colors.accent.copy(alpha = 0.15f)
                            ),

                            shape = RoundedCornerShape(12.dp)
                        ) {

                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = colors.accent
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Add \"${searchQuery.ifBlank { "Unnamed course" }}\"",
                                    color = colors.accent
                                )
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog && selectedCourse != null) {


                AlertDialog(

                    onDismissRequest = {
                        showDeleteDialog = false
                        selectedCourse = null
                    },


                    title = {
                        Text("Delete course?")
                    },


                    text = {
                        Text(
                            "Do you want to delete this saved course?"
                        )
                    },


                    confirmButton = {

                        TextButton(

                            onClick = {

                                selectedCourse?.let {

                                    onDeleteCourse(it)
                                }


                                showDeleteDialog = false
                                selectedCourse = null
                            }

                        ) {

                            Text("Delete")
                        }
                    },


                    dismissButton = {

                        TextButton(

                            onClick = {

                                showDeleteDialog = false
                                selectedCourse = null
                            }

                        ) {

                            Text("Cancel")
                        }
                    }
                )
            }


            // SCROLLBAR
            val totalItems = listState.layoutInfo.totalItemsCount
            val visibleItems = listState.layoutInfo.visibleItemsInfo.size
            val density = LocalDensity.current

            if (totalItems > visibleItems && visibleItems > 0) {

                BoxWithConstraints(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp)
                        .fillMaxHeight()
                ) {

                    val itemHeight =
                        listState.layoutInfo.visibleItemsInfo
                            .firstOrNull()
                            ?.size
                            ?.toFloat()
                            ?: 1f


                    val totalScroll =
                        (totalItems * itemHeight) -
                                with(density) { maxHeight.toPx() }


                    val currentScroll =
                        (listState.firstVisibleItemIndex * itemHeight) +
                                listState.firstVisibleItemScrollOffset


                    val progress =
                        (currentScroll / totalScroll)
                            .coerceIn(0f, 1f)


                    val scrollbarHeight =
                        (maxHeight *
                                (visibleItems.toFloat() / totalItems.toFloat()))
                            .coerceIn(
                                minimumValue = 20.dp,
                                maximumValue = 28.dp
                            )


                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(scrollbarHeight)
                            .offset(
                                y = (maxHeight - scrollbarHeight) * progress
                            )
                            .background(
                                colors.accent.copy(alpha = 0.8f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}