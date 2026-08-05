package com.example.discly

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.BackHandler


@Composable
fun MainScreen(
    themeMode: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    colors: AppColors,

) {

    var selectedResult by remember {
        mutableStateOf<GameResult?>(null)
    }

    var newCourseName by remember {
        mutableStateOf("")
    }


    var currentScreen by remember {
        mutableStateOf<AppScreen>(AppScreen.CourseSelect)
    }

    var players by remember {
        mutableStateOf(listOf<String>())
    }

    var totalHoles by remember {
        mutableStateOf(18)
    }

    var courseName by remember {
        mutableStateOf("")
    }

    var selectedCourse by remember {
        mutableStateOf<Course?>(null)
    }

    var savedCourses by remember {
        mutableStateOf<List<Course>>(emptyList())
    }


    val context = LocalContext.current

    var unfinishedGame by remember {
        mutableStateOf<SavedGame?>(null)
    }


    LaunchedEffect(Unit) {


        AppStorage.initializeCourses(
            context,
            CoursesDB
        )


        savedCourses =
            AppStorage.loadCourses(context)


        unfinishedGame =
            AppStorage.loadCurrentGame(context)
    }


    fun refreshUnfinishedGame() {

        unfinishedGame =
            AppStorage.loadCurrentGame(context)
    }

    fun refreshCourses() {

        val customCourses =
            AppStorage.loadCourses(context)

        savedCourses =
            CoursesDB + customCourses
    }

    BackHandler {

        when (currentScreen) {

            AppScreen.GameResult -> {

                currentScreen = AppScreen.Games

            }

            AppScreen.CourseSelect -> {
                // Päävalikossa ei tehdä mitään
            }


            AppScreen.Settings -> {

                currentScreen = AppScreen.CourseSelect
            }


            AppScreen.Games -> {

                currentScreen = AppScreen.CourseSelect
            }


            AppScreen.Start -> {

                currentScreen = AppScreen.CourseSelect
            }


            AppScreen.Game -> {

                currentScreen = AppScreen.CourseSelect
            }

            AppScreen.CourseStats -> {

                currentScreen = AppScreen.CourseSelect
            }

        }
    }

    when (currentScreen) {

        AppScreen.CourseSelect -> {

            CourseSelectScreen(
                courses = savedCourses,
                colors = colors,

                unfinishedGame = unfinishedGame,

                onContinueGame = {

                    val game = unfinishedGame

                    if (game != null) {

                        players = game.players
                        totalHoles = game.totalHoles
                        courseName = game.courseName

                        currentScreen = AppScreen.Game
                    }
                },


                onCourseSelected = { course ->

                    selectedCourse = course
                    courseName = course.name
                    totalHoles = course.holeCount
                    players = listOf("Player 1")

                    val newGame = SavedGame(
                        courseName = course.name,
                        players = players,
                        scores = players.map {
                            List(course.holeCount) { 0 }
                        },
                        pars = course.pars,
                        totalHoles = course.holeCount,
                        currentHole = 0,
                        startTime = System.currentTimeMillis()
                    )

                    AppStorage.saveCurrentGame(context, newGame)

                    // 🔴 KRITTIINEN
                    unfinishedGame = newGame

                    //currentScreen = AppScreen.Game
                    currentScreen = AppScreen.CourseStats
                },


                onNewCourse = { name ->

                    newCourseName = name

                    currentScreen = AppScreen.Start
                },


                onSettings = {
                    currentScreen = AppScreen.Settings
                },


                onViewGames = {
                    currentScreen = AppScreen.Games
                },


                onDeleteUnfinishedGame = {
                    AppStorage.deleteCurrentGame(context)
                    unfinishedGame = null
                },
                onDeleteCourse = { course ->

                    AppStorage.deleteCourse(
                        context,
                        course
                    )


                    savedCourses =
                        AppStorage.loadCourses(context)
                }
            )
        }

        AppScreen.Start -> {

            StartScreen(

                initialCourseName = newCourseName,

                colors = colors,


                onSaveCourse = { course ->


                    val old =
                        AppStorage.loadCourses(context)


                    if(old.any {
                            it.name.equals(course.name, ignoreCase = true)
                        }) {

                        false

                    } else {


                        AppStorage.saveCourses(
                            context,
                            old + course
                        )


                        savedCourses =
                            CoursesDB + old + course


                        true
                    }

                },

                onStartGame = { course ->


                    selectedCourse = course

                    courseName = course.name

                    totalHoles = course.holeCount


                    players = listOf("Player 1")


                    val newGame = SavedGame(

                        courseName = course.name,

                        players = players,

                        scores = players.map {

                            List(course.holeCount) { 0 }

                        },

                        pars = course.pars,

                        totalHoles = course.holeCount,

                        currentHole = 0,
                        startTime = System.currentTimeMillis()
                    )


                    AppStorage.saveCurrentGame(
                        context,
                        newGame
                    )


                    unfinishedGame = newGame


                    currentScreen = AppScreen.Game
                },


                onBack = {

                    currentScreen =
                        AppScreen.CourseSelect

                }

            )
        }

        AppScreen.Game -> {

            GameScreen(
                players = players,
                totalHoles = totalHoles,
                courseName = courseName,

                pars = unfinishedGame?.pars
                    ?: selectedCourse?.pars
                    ?: List(totalHoles){3},

                savedScores = unfinishedGame?.scores,

                startHole = unfinishedGame?.currentHole ?: 0,

                colors = colors,

                startTime = unfinishedGame?.startTime
                    ?: System.currentTimeMillis(),

                onGameFinished = {
                    refreshUnfinishedGame()
                },


                onExitToMenu = {

                    refreshUnfinishedGame()

                    currentScreen = AppScreen.CourseSelect

                    players = emptyList()

                    totalHoles = 18

                    courseName = ""

                    selectedCourse = null
                }
            )
        }

        AppScreen.Settings -> {

            SettingsScreen(
                currentTheme = themeMode,

                colors = colors,

                onThemeSelected = {
                    onThemeSelected(it)
                },

                onBack = {
                    currentScreen = AppScreen.CourseSelect
                }
            )
        }

        AppScreen.Games -> {
            GamesScreen(
                colors = colors,
                onBack = { currentScreen = AppScreen.CourseSelect },
                onOpenGame = { game ->
                    selectedResult = game
                    currentScreen = AppScreen.GameResult
                }
            )
        }
        AppScreen.GameResult -> {

            selectedResult?.let { game ->

                SummaryScreen(
                    players = listOf("Player 1"),
                    scoreTable = game.scores,
                    pars = game.pars,
                    colors = colors,
                    courseName = game.courseName,

                    onBack = {
                        currentScreen = AppScreen.Games
                    },

                    onMenu = {
                        currentScreen = AppScreen.CourseSelect
                    }
                )
            }
        }
        AppScreen.CourseStats -> {

            selectedCourse?.let { course ->

                val rounds = AppStorage.loadHistory(context)

                CourseStatsScreen(
                    rounds = rounds,
                    courseName = course.name,
                    colors = colors,

                    onBack = {
                        currentScreen = AppScreen.CourseSelect
                    },

                    onStartGame = {

                        val newGame = SavedGame(
                            courseName = course.name,
                            players = players,
                            scores = players.map {
                                List(course.holeCount) { 0 }
                            },
                            pars = course.pars,
                            totalHoles = course.holeCount,
                            currentHole = 0,
                            startTime = System.currentTimeMillis()
                        )

                        AppStorage.saveCurrentGame(context, newGame)

                        unfinishedGame = newGame

                        currentScreen = AppScreen.Game
                    }
                )
            }
        }

    }
}