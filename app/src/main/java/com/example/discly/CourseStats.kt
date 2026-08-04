package com.example.discly


data class HoleStats(
    val holeNumber: Int,
    val best: Int,
    val average: Double,
    val worst: Int
)


data class CourseStats(

    val courseName: String,

    val totalRounds: Int,

    val averageScore: Double,

    val toughestHoles: List<Int>,

    val averageDurationSec: Double?,

    val holes: List<HoleStats>
)