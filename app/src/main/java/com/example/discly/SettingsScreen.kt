package com.example.discly

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.discly.DiscType
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
    onBack: () -> Unit,
    colors: AppColors,
    selectedDisc: DiscType,
    onDiscSelected: (DiscType) -> Unit
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


    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {


        // HEADER (kiinteä, ei scrollaa)
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

        Spacer(modifier = Modifier.height(20.dp))


        // LOPPUSISÄLTÖ (scrollattava)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),

            verticalArrangement = Arrangement.spacedBy(20.dp)

        ) {



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


            // DISC GRID
            // Huom: LazyVerticalGrid vaihdettu tavalliseksi Row/Column-gridiksi,
            // koska laiskaa gridiä ei voi laittaa turvallisesti toisen
            // scrollattavan kontin (verticalScroll) sisään.
            DiscType.entries.chunked(3).forEach { rowDiscs ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    rowDiscs.forEach { disc ->

                        val isSelected = disc == selectedDisc

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.08f else 1f,
                            label = ""
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .graphicsLayer {
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .clickable {
                                        onDiscSelected(disc)

                                        scope.launch {
                                            AppStorage.setSelectedDisc(context, disc)
                                        }
                                    },
                                shape = MaterialTheme.shapes.medium,
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.card
                                ),
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = if (isSelected) 10.dp else 3.dp
                                )
                            ) {

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Image(
                                        painter = painterResource(disc.drawable),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(10.dp)
                                    )

                                    // 🔥 SELECTED BORDER (clean)
                                    if (isSelected) {
                                        Box(
                                            Modifier
                                                .matchParentSize()
                                                .border(
                                                    width = 3.dp,
                                                    color = colors.accent,
                                                    shape = MaterialTheme.shapes.medium
                                                )
                                        )

                                        // ✅ SELECTED CHECKMARK BADGE
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .size(20.dp)
                                                .background(
                                                    color = colors.accent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Valittu",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = disc.name,
                                color = colors.text,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }

                    // Täytetään vajaa rivi, ettei viimeisen rivin kortit veny leveämmiksi
                    repeat(3 - rowDiscs.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }


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
}