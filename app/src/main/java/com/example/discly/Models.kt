package com.example.discly

data class RoundResult(
    val courseName: String,
    val scores: List<Int>,
    val pars: List<Int>,
    val durationSec: Int,
    val timestamp: Long
)
