package com.example.discly

data class GameResult(

    val courseName: String,

    val players: List<String>,

    val scores: List<List<Int>>,

    val pars: List<Int>,

    val timestamp: Long = System.currentTimeMillis()
)