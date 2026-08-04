package com.example.discly


sealed class AppScreen {

    data object CourseSelect : AppScreen()

    data object Start : AppScreen()

    data object Game : AppScreen()

    data object Settings : AppScreen()

    data object Games : AppScreen()


    data object CourseStats : AppScreen()

    data object GameResult : AppScreen()
}