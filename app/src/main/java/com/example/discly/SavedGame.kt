package com.example.discly

data class SavedGame(

    val courseName: String,

    val players: List<String>,

    val scores: List<List<Int>>,

    val pars: List<Int>,

    val totalHoles: Int,

    val currentHole: Int,

    val timestamp: Long = System.currentTimeMillis(),

    val startTime: Long = System.currentTimeMillis()
)