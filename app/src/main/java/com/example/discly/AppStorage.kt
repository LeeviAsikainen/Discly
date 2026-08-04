package com.example.discly

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

// DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit

// Coroutines / Flow
import kotlinx.coroutines.flow.map

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



// JSON

// 🔹 YKSI DataStore instanssi
val Context.dataStore by preferencesDataStore(name = "settings")

object AppStorage {

    fun loadResults(
        context: Context
    ): List<GameResult> {

        return loadHistory(context)
    }

    fun saveResult(context: Context, result: GameResult) {

        val prefs = context.getSharedPreferences("discly_prefs", Context.MODE_PRIVATE)

        val existing = loadResults(context).toMutableList()

        existing.add(result)

        val json = Gson().toJson(existing)

        prefs.edit().putString("results", json).apply()
    }

    // =========================
    // 🔧 SETTINGS (DataStore)
    // =========================

    private val THEME_KEY = stringPreferencesKey("theme")

    fun getTheme(context: Context) =
        context.dataStore.data.map { prefs ->
            runCatching {
                ThemeMode.valueOf(prefs[THEME_KEY] ?: "")
            }.getOrDefault(ThemeMode.DESERT)
        }

    suspend fun setTheme(context: Context, theme: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_KEY] = theme.name
        }
    }

    // =========================
    // 🎮 GAMES (JSON FILE)
    // =========================


    private const val CURRENT_GAME =
        "current_game.json"


    private const val HISTORY =
        "game_history.json"



    // -------------------------
    // CURRENT GAME
    // -------------------------


    fun saveCurrentGame(
        context: Context,
        game: SavedGame
    ) {

        val json = JSONObject()


        json.put(
            "courseName",
            game.courseName
        )


        json.put(
            "totalHoles",
            game.totalHoles
        )


        json.put(
            "currentHole",
            game.currentHole
        )


        json.put(
            "timestamp",
            game.timestamp
        )

        json.put(
            "startTime",
            game.startTime
        )



        json.put(
            "players",
            JSONArray(game.players)
        )


        json.put(
            "pars",
            JSONArray(game.pars)
        )


        val scores = JSONArray()

        game.scores.forEach { player ->

            scores.put(
                JSONArray(player)
            )
        }


        json.put(
            "scores",
            scores
        )


        context.openFileOutput(
            CURRENT_GAME,
            Context.MODE_PRIVATE
        )
            .use {

                it.write(
                    json.toString().toByteArray()
                )
            }
    }



    fun loadCurrentGame(
        context: Context
    ): SavedGame? {




        return try {


            val text =
                context.openFileInput(
                    CURRENT_GAME
                )
                    .bufferedReader()
                    .readText()


            val json =
                JSONObject(text)



            SavedGame(

                courseName =
                    json.getString("courseName"),


                players =
                    json.getJSONArray("players")
                        .toList(),


                scores =
                    json.getJSONArray("scores")
                        .toScoreList(),


                pars =
                    json.getJSONArray("pars")
                        .toIntList(),


                totalHoles =
                    json.getInt("totalHoles"),


                currentHole =
                    json.getInt("currentHole"),


                timestamp =
                    json.getLong("timestamp"),

                startTime =
                    if (json.has("startTime"))
                        json.getLong("startTime")
                    else
                        System.currentTimeMillis()
            )




        } catch (e: Exception) {

            null
        }

    }



    fun deleteCurrentGame(
        context: Context
    ) {

        context.deleteFile(
            CURRENT_GAME
        )
    }



    fun deleteGame(context: Context, game: GameResult) {

        val games = loadHistory(context).toMutableList()

        games.removeAll {
            it.timestamp == game.timestamp
        }

        saveHistory(context, games)
    }

    fun saveHistory(
        context: Context,
        games: List<GameResult>
    ) {

        val array = JSONArray()

        games.forEach {

            val json = JSONObject()

            json.put("courseName", it.courseName)
            json.put("timestamp", it.timestamp)
            json.put("durationSec", it.durationSec)
            json.put("players", JSONArray(it.players))
            json.put("pars", JSONArray(it.pars))

            val scores = JSONArray()

            it.scores.forEach { player ->
                scores.put(JSONArray(player))
            }

            json.put("scores", scores)

            array.put(json)
        }

        context.openFileOutput(
            HISTORY,
            Context.MODE_PRIVATE
        ).use {
            it.write(array.toString().toByteArray())
        }
    }




    // -------------------------
    // HISTORY
    // -------------------------


    fun saveGameResult(
        context: Context,
        result: GameResult
    ) {


        val games =
            loadHistory(context)
                .toMutableList()


        games.add(result)


        val array = JSONArray()


        games.forEach {

            val json = JSONObject()

            json.put(
                "courseName",
                it.courseName
            )


            json.put(
                "timestamp",
                it.timestamp
            )

            json.put(
                "durationSec",
                it.durationSec
            )


            json.put(
                "players",
                JSONArray(it.players)
            )


            json.put(
                "pars",
                JSONArray(it.pars)
            )


            val scores =
                JSONArray()

            it.scores.forEach { player ->

                scores.put(
                    JSONArray(player)
                )
            }


            json.put(
                "scores",
                scores
            )


            array.put(json)
        }



        context.openFileOutput(
            HISTORY,
            Context.MODE_PRIVATE
        )
            .use {

                it.write(
                    array.toString().toByteArray()
                )
            }
    }



    fun loadHistory(
        context: Context
    ): List<GameResult> {


        return try {


            val text =
                context.openFileInput(HISTORY)
                    .bufferedReader()
                    .readText()


            val array =
                JSONArray(text)


            List(array.length()) {


                val json =
                    array.getJSONObject(it)


                GameResult(

                    courseName =
                        json.getString("courseName"),

                    players =
                        json.getJSONArray("players")
                            .toList(),

                    scores =
                        json.getJSONArray("scores")
                            .toScoreList(),

                    pars =
                        json.getJSONArray("pars")
                            .toIntList(),

                    timestamp =
                        json.getLong("timestamp"),

                    durationSec =
                        if (json.has("durationSec"))
                            json.getLong("durationSec")
                        else
                            null
                )
            }


        } catch(e:Exception){

            emptyList()
        }
    }

// =========================
// 🥏 COURSES
// =========================

    private const val COURSES =
        "courses.json"


    fun initializeCourses(
        context: Context,
        defaultCourses: List<Course>
    ) {

        val savedCourses = loadCourses(context)

        if (savedCourses.isEmpty()) {

            saveCourses(
                context,
                defaultCourses
            )
        }
    }

    fun saveCourses(
        context: Context,
        courses: List<Course>
    ) {

        val array = JSONArray()


        courses.forEach { course ->

            val json = JSONObject()

            json.put(
                "name",
                course.name
            )

            json.put(
                "custom",
                course.custom
            )


            json.put(
                "pars",
                JSONArray(course.pars)
            )


            array.put(json)
        }


        context.openFileOutput(
            COURSES,
            Context.MODE_PRIVATE
        ).use {

            it.write(
                array.toString().toByteArray()
            )
        }
    }



    fun loadCourses(
        context: Context
    ): List<Course> {


        return try {


            val text =
                context.openFileInput(COURSES)
                    .bufferedReader()
                    .readText()


            val array =
                JSONArray(text)



            List(array.length()) {


                val json =
                    array.getJSONObject(it)


                Course(

                    name =
                        json.getString("name"),


                    pars =
                        json.getJSONArray("pars")
                            .toIntList(),


                    custom =
                        json.getBoolean("custom")
                )
            }


        } catch(e: Exception){

            emptyList()
        }
    }



    fun deleteCourse(
        context: Context,
        course: Course
    ) {

        val courses = loadCourses(context)
            .toMutableList()

        courses.removeAll {
            it.name == course.name
        }

        saveCourses(
            context,
            courses
        )
    }

}



// JSON apurit

private fun JSONArray.toIntList(): List<Int>{

    return List(length()){

        getInt(it)
    }
}



private fun JSONArray.toList(): List<String>{

    return List(length()){

        getString(it)
    }
}



private fun JSONArray.toScoreList()
        : List<List<Int>> {


    return List(length()){


        getJSONArray(it)
            .toIntList()

    }
}