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
        mutableStateOf(CoursesDB)
    }


    val context = LocalContext.current

    var unfinishedGame by remember {
        mutableStateOf<SavedGame?>(null)
    }


    LaunchedEffect(Unit) {

        val customCourses =
            AppStorage.loadCourses(context)

        savedCourses =
            CoursesDB + customCourses

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
                        currentHole = 0
                    )

                    AppStorage.saveCurrentGame(context, newGame)

                    // 🔴 KRITTIINEN
                    unfinishedGame = newGame

                    currentScreen = AppScreen.Game
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
                onDeleteCourse = {

                    AppStorage.deleteCourse(
                        context,
                        it
                    )

                    refreshCourses()
                }
            )
        }

        AppScreen.Start -> {

            StartScreen(
                initialCourseName = newCourseName
            ) { p, h, c, save ->


                players = p
                totalHoles = h
                courseName = c


                if(save){

                    val oldCourses =
                        AppStorage.loadCourses(context)


                    val exists =
                        oldCourses.any {
                            it.name.equals(
                                c,
                                ignoreCase = true
                            )
                        }


                    if(exists){

                        return@StartScreen false
                    }


                    val newCourse =
                        Course(
                            name = c,
                            pars = List(h){3},
                            custom = true
                        )


                    AppStorage.saveCourses(
                        context,
                        oldCourses + newCourse
                    )


                    savedCourses =
                        CoursesDB + oldCourses + newCourse
                }


                currentScreen = AppScreen.Game

                true
            }
        }

        AppScreen.Game -> {

            GameScreen(
                players = players,
                totalHoles = totalHoles,
                courseName = courseName,

                pars = selectedCourse?.pars
                    ?: List(totalHoles){3},

                savedScores = unfinishedGame?.scores,

                startHole = unfinishedGame?.currentHole ?: 0,

                colors = colors,

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
                onBack = { currentScreen = AppScreen.CourseSelect }
            )
        }

    }
}