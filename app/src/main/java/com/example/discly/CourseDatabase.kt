package com.example.discly

data class Course(
    val name: String,
    val pars: List<Int>
) {

    val holeCount: Int
        get() = pars.size
}

val CoursesDB = listOf(

    // 🔹 Puijo
    Course(
        name = "Puijo DiscGolf",

        pars = listOf(
            4,3,3,4,4,3,3,3,3,
            3,3,4,3,3,3,3,3,4
        )
    ),

    // 🔹 Peikkometsä
    Course(
        name = "Peikkometsä",

        pars = listOf(
            3,3,3,3,3,3,3,3,3
        )
    ),

    // 🔹 Huuhanmetsä
    Course(
        name = "Huuhanmetsä",

        pars = listOf(
            3, 4, 3, 4, 4, 3,
            3, 4, 3, 3, 3, 3,
            4, 3, 3, 3, 3, 3,
            3, 3, 3, 4, 3, 4
        )
    ),

    // 🔹 Riemurinne
    Course(
        name = "Riemurinne",

        pars = listOf(
            3,3,3,3,3,3,3,3,3
        )
    ),

    // 🔹 Päiväranta
    Course(
        name = "Päiväranta",

        pars = listOf(
            3,3,3,3,3,3,3,3,3
        )
    ),

    // 🔹 Tahko
    Course(
        name = "Tahko DiscGolf Park",

        pars = listOf(
            4,3,3,4,4,3,4,3,3,
            4,3,4,3,3,4,3,4,5
        )
    ),

    // 🔹 Tarinalaakso Vanha
    Course(
        name = "Tarinalaakso Old",

        pars = listOf(
            3,3,3,3,3,3,3,3,3,
            3,3,3,3,3,3,3,3,3
        )
    ),

    // 🔹 Tarinalaakso Uusi
    Course(
        name = "Tarinalaakso New",

        pars = listOf(
            4,3,3,4,3,3,4,3,3,
            4,3,4,3,3,4,3,4,4
        )
    )
)