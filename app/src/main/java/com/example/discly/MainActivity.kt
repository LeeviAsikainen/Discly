package com.example.discly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.example.discly.ui.theme.uDiscTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val context = LocalContext.current
            val scope = rememberCoroutineScope()

            var themeMode by remember {
                mutableStateOf(ThemeMode.DESERT)
            }


            // Lataa tallennettu teema
            LaunchedEffect(Unit) {
                AppStorage.getTheme(context).collect {
                    themeMode = it
                }
            }


            // Muodostetaan värit valitun teeman mukaan
            val colors = getTheme(themeMode)


            uDiscTheme {

                MainScreen(
                    themeMode = themeMode,
                    onThemeSelected = { newTheme ->

                        themeMode = newTheme

                        scope.launch {
                            AppStorage.setTheme(
                                context,
                                newTheme
                            )
                        }
                    },
                    colors = colors
                )

            }
        }
    }
}