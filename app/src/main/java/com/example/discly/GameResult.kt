package com.example.discly

data class GameResult(

    val courseName: String,

    val players: List<String>,

    val scores: List<List<Int>>,

    val pars: List<Int>,

    val durationSec: Long? = null,

    val timestamp: Long = System.currentTimeMillis()
)