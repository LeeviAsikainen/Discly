package com.example.udisc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    colors: AppColors
) {

    var themeExpanded by remember {
        mutableStateOf(false)
    }

    var languageExpanded by remember {
        mutableStateOf(false)
    }

    var selectedLanguage by remember {
        mutableStateOf("English")
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(20.dp)

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
                text = "Settings",
                color = colors.text,
                style = MaterialTheme.typography.headlineMedium
            )
        }



        // LANGUAGE
        Row(

            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text(
                text = "Language",
                color = colors.text,
                style = MaterialTheme.typography.titleMedium
            )


            Box {

                Button(

                    onClick = {
                        languageExpanded = true
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent
                    )

                ) {

                    Text(selectedLanguage)
                }



                DropdownMenu(

                    expanded = languageExpanded,

                    onDismissRequest = {
                        languageExpanded = false
                    }

                ) {


                    listOf(
                        "English",
                        "Suomi"
                    ).forEach { language ->


                        DropdownMenuItem(

                            text = {
                                Text(language)
                            },


                            onClick = {

                                // TODO:
                                // Kielenvaihto myöhemmin

                                selectedLanguage = language

                                languageExpanded = false
                            }

                        )
                    }
                }
            }
        }




        // THEME
        Row(

            modifier = Modifier.fillMaxWidth(),

            verticalAlignment = Alignment.CenterVertically,

            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Text(
                text = "Theme",
                color = colors.text,
                style = MaterialTheme.typography.titleMedium
            )



            Box {

                Button(

                    onClick = {
                        themeExpanded = true
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent
                    )

                ) {

                    Text(
                        currentTheme.name
                    )
                }



                DropdownMenu(

                    expanded = themeExpanded,

                    onDismissRequest = {
                        themeExpanded = false
                    }

                ) {


                    ThemeMode.entries.forEach { theme ->


                        DropdownMenuItem(

                            text = {

                                Text(
                                    theme.name
                                )

                            },


                            onClick = {

                                onThemeSelected(theme)

                                themeExpanded = false

                            }

                        )
                    }
                }
            }
        }



        Spacer(
            modifier = Modifier.weight(1f)
        )



        Button(

            onClick = onBack,

            modifier = Modifier.fillMaxWidth(),

            colors = ButtonDefaults.buttonColors(
                containerColor = colors.accent
            )

        ) {

            Text("Back")

        }
    }
}