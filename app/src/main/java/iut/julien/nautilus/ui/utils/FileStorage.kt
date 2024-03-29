package iut.julien.nautilus.ui.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import kotlin.coroutines.coroutineContext

class FileStorage {
    companion object {
        private const val FILE_NAME = "dives.json"


        //    Data persistence - Liked dives stored in a JSON file

        fun addFavoriteDive(diveID: String, context: Context) {

            context.openFileOutput(FILE_NAME, Context.MODE_APPEND).use {
                it.write("$diveID,".toByteArray())
            }

//            Display in the console the content of the file
            val file = context.getFileStreamPath(FILE_NAME)
            if (file == null || !file.exists()) {
                return
            }

            println(file.readText())
        }

        fun removeFavoriteDive(diveID: String, context: Context) {
            val file = context.getFileStreamPath(FILE_NAME)

            if (file == null || !file.exists()) {
                return
            }

            val dives = file.readText().split(",")

            val newDives =
                dives.filter { it != diveID && it.isNotBlank() && !it.contains("{") && it.length <= 4 }

            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use {
                it.write(newDives.joinToString(separator = ",", postfix = ",").toByteArray())
            }
        }

        fun getFavoriteDives(context: Context): List<String> {
            val file = context.getFileStreamPath(FILE_NAME)

            if (file == null || !file.exists()) {
                return emptyList()
            }

            val dives = file.readText().split(",")

            return dives.filter { it.isNotBlank() && !it.contains("{") && it.length <= 4 }
        }
    }
}