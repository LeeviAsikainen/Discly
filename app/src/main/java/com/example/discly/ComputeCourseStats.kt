package com.example.discly.domain

import com.example.discly.*


fun computeCourseStats(
    rounds: List<GameResult>,
    courseName: String
): CourseStats {


    val courseRounds =
        rounds.filter {
            it.courseName == courseName
        }


    if (courseRounds.isEmpty()) {

        return CourseStats(
            courseName = courseName,
            totalRounds = 0,
            averageScore = 0.0,
            toughestHoles = emptyList(),
            averageDurationSec = null,
            holes = emptyList()
        )
    }


    val holeCount =
        courseRounds.first().pars.size



    val holeStats =
        (0 until holeCount).map { index ->


            val scores =
                courseRounds.flatMap { round ->

                    round.scores.flatMap { player ->

                        listOfNotNull(
                            player.getOrNull(index)
                                ?.takeIf { it > 0 }
                        )
                    }
                }



            HoleStats(

                holeNumber = index + 1,

                best =
                    scores.minOrNull()
                        ?: 0,


                average =
                    if (scores.isNotEmpty())
                        scores.average()
                    else
                        0.0,


                worst =
                    scores.maxOrNull()
                        ?: 0
            )
        }



    // 🔹 Kokonaistulos ilman nollia

    val totalScores =
        courseRounds.flatMap { round ->

            round.scores.map { player ->

                player.filter { it > 0 }
                    .sum()

            }
        }
            .filter { it > 0 }



    val averageScore =
        if (totalScores.isNotEmpty())
            totalScores.average()
        else
            0.0



    // 🔹 Vaikeimmat väylät

    val highestAverage =
        holeStats.maxOfOrNull {
            it.average
        }


    val toughestHoles =
        if (highestAverage != null) {

            holeStats
                .filter {
                    it.average == highestAverage
                }
                .map {
                    it.holeNumber
                }
                .take(3)

        } else {

            emptyList()
        }

    val durations =
        courseRounds.mapNotNull {
            it.durationSec
        }


    val averageDuration =
        if (durations.isNotEmpty())
            durations.average()
        else
            null


    return CourseStats(

        courseName = courseName,

        totalRounds = courseRounds.size,

        averageScore = averageScore,

        toughestHoles = toughestHoles,

        averageDurationSec = averageDuration,

        holes = holeStats
    )
}