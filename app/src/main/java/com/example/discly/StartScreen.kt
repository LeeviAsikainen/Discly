package com.example.discly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


@Composable
fun StartScreen(
    initialCourseName: String = "",

    colors: AppColors,

    onSaveCourse: (Course) -> Boolean,
    onStartGame: (Course) -> Unit,

    onBack: () -> Unit
) {


    var courseName by remember(initialCourseName) {
        mutableStateOf(initialCourseName)
    }


    var holeCountText by remember {
        mutableStateOf("18")
    }


    var pars by remember {
        mutableStateOf(
            List(18) { 3 }
        )
    }


    var courseError by remember {
        mutableStateOf(false)
    }



    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {


        // HEADER
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.accent
                )
            }


            Text(

                text = "Create Course",

                style = MaterialTheme.typography.headlineMedium,

                color = colors.text
            )
        }


        OutlinedTextField(

            value = courseName,

            onValueChange = {

                courseName = it
                courseError = false

            },

            label = {

                Text("Course Name")

            },

            singleLine = true,

            modifier = Modifier.fillMaxWidth()

        )



        if(courseError){

            Text(

                text = "A course with this name already exists",

                color = MaterialTheme.colorScheme.error

            )
        }




        // Väylämäärä

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Number of Holes",
                color = colors.text
            )


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {


                IconButton(
                    onClick = {

                        val current =
                            holeCountText.toIntOrNull() ?: 18

                        if(current > 1){

                            holeCountText =
                                (current - 1).toString()

                            pars =
                                pars.take(current - 1)
                        }

                    }
                ) {
                    Text(
                        "-",
                        color = colors.accent
                    )
                }



                OutlinedTextField(

                    value = holeCountText,

                    onValueChange = { value ->

                        val number =
                            value.filter {
                                it.isDigit()
                            }


                        holeCountText = number


                        val holes =
                            number.toIntOrNull()


                        if(holes != null &&
                            holes in 1..36
                        ){

                            when {

                                holes > pars.size -> {

                                    pars =
                                        pars + List(
                                            holes - pars.size
                                        ){
                                            3
                                        }

                                }


                                holes < pars.size -> {

                                    pars =
                                        pars.take(holes)

                                }
                            }
                        }

                    },

                    singleLine = true,

                    modifier = Modifier
                        .width(80.dp),

                    textStyle = LocalTextStyle.current.copy(
                        color = colors.text
                    ),

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )

                )



                IconButton(
                    onClick = {

                        val current =
                            holeCountText.toIntOrNull() ?: 18


                        if(current < 36){

                            holeCountText =
                                (current + 1).toString()


                            pars =
                                pars + 3
                        }

                    }
                ) {

                    Text(
                        "+",
                        color = colors.accent
                    )
                }
            }
        }



        Divider()



        LazyColumn(

            modifier = Modifier
                .weight(1f),

            verticalArrangement = Arrangement.spacedBy(8.dp)

        ){

            itemsIndexed(pars){ index, par ->


                Card(

                    colors = CardDefaults.cardColors(

                        containerColor = colors.card

                    ),

                    modifier = Modifier.fillMaxWidth()

                ){

                    Row(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),

                        horizontalArrangement = Arrangement.SpaceBetween,

                        verticalAlignment = Alignment.CenterVertically

                    ){



                        Text(

                            text = "Hole ${index + 1}",

                            color = colors.text

                        )



                        Row(

                            verticalAlignment = Alignment.CenterVertically

                        ){


                            IconButton(

                                onClick = {

                                    pars =
                                        pars.toMutableList()
                                            .also {

                                                it[index] =
                                                    (it[index] - 1)
                                                        .coerceAtLeast(1)

                                            }

                                }

                            ){

                                Text(

                                    "-",

                                    color = colors.accent

                                )

                            }



                            Text(

                                text = par.toString(),

                                color = colors.text,

                                style = MaterialTheme.typography.titleLarge

                            )



                            IconButton(

                                onClick = {

                                    pars =
                                        pars.toMutableList()
                                            .also {

                                                it[index] =
                                                    (it[index] + 1)
                                                        .coerceAtMost(10)

                                            }

                                }

                            ){

                                Text(

                                    "+",

                                    color = colors.accent

                                )

                            }

                        }

                    }

                }

            }

        }



        Button(

            onClick = {


                val course = Course(

                    name =
                        if(courseName.isBlank())
                            "Unnamed Course"
                        else
                            courseName,


                    pars = pars,

                    custom = true

                )


                val success =
                    onSaveCourse(course)


                if(success){

                    onStartGame(course)

                }
                else{

                    courseError = true

                }


            },

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(

                containerColor = colors.accent

            )

        ){

            Text("Save Course")

        }
    }
}