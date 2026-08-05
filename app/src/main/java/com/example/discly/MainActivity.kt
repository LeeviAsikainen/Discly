package com.example.discly

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.launch
import com.example.discly.ui.theme.uDiscTheme


class MainActivity : ComponentActivity() {

    @Composable
    fun SetSystemBarsColor(color: Color) {
        val view = LocalView.current

        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                window.navigationBarColor = color.toArgb()
            }
        }
    }



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

                SetSystemBarsColor(colors.background)


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