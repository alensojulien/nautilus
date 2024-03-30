package iut.julien.nautilus.ui.utils

import android.content.Context

/**
 * FileStorage class used to store liked dives in a JSON file for example
 */
class FileStorage {
    /**
     * Companion object to store the static methods of the FileStorage class
     */
    companion object {
        /**
         * Constant for the file name
         */
        private const val FILE_NAME = "dives.json"

// Data persistence

        /**
         * Method to add a favorite dive to the file
         *
         * @param diveID the ID of the dive to add
         * @param context the context of the application
         */
        fun addFavoriteDive(diveID: String, context: Context) {

            context.openFileOutput(FILE_NAME, Context.MODE_APPEND).use {
                it.write("$diveID,".toByteArray())
            }

//            Display in the console the content of the file for debug purposes
            val file = context.getFileStreamPath(FILE_NAME)
            if (file == null || !file.exists()) {
                return
            }

            println(file.readText())
        }

        /**
         * Method to remove a favorite dive from the file
         *
         * @param diveID the ID of the dive to remove
         * @param context the context of the application
         */
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

        /**
         * Method to get the list of favorite dives from the file
         *
         * @param context the context of the application
         * @return the list of favorite dives
         */
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