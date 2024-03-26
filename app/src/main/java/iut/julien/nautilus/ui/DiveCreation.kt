package iut.julien.nautilus.ui

import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import iut.julien.nautilus.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.time.LocalDate

class DiveCreation {
    @Composable
    fun DiveCreationScreen() {
        val locationList = remember {
            mutableStateListOf("")
        }
        val boatList = remember {
            mutableStateListOf("")
        }
        val levelList = remember {
            mutableStateListOf("")
        }
        val director = remember {
            mutableStateListOf("")
        }
        val security = remember {
            mutableStateListOf("")
        }
        val pilote = remember {
            mutableStateListOf("")
        }
        LaunchedEffect(Unit) {
            val list = requestToAPIData(
                URL("https://dev-sae301grp3.users.info.unicaen.fr/api/divinglocation"),
                "DL_NAME"
            )
            locationList.addAll(list)
            val listBoat = requestToAPIData(
                URL("https://dev-sae301grp3.users.info.unicaen.fr/api/boat"),
                "BO_NAME"
            )
            boatList.addAll(listBoat)
            val listLevel = requestToAPIData(
                URL("https://dev-sae301grp3.users.info.unicaen.fr/api/prerogative"),
                "PRE_CODE"
            )
            levelList.addAll(listLevel)
            val listDirector = getUserRole("DIRECTOR")
            director.addAll(listDirector)
            val listPilote = getUserRole("PILOTE")
            pilote.addAll(listPilote)
            val listSecurity = getUserRole("SECURITY")
            security.addAll(listSecurity)
        }
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context).inflate(R.layout.activity_main, null)
                val locationSpinner = view.findViewById<Spinner>(R.id.DS_LOCATION)
                val boatSpinner = view.findViewById<Spinner>(R.id.DS_BOAT)
                val levelSpinner = view.findViewById<Spinner>(R.id.DS_LEVEL)
                val directorSpinner = view.findViewById<Spinner>(R.id.DS_DIRECTOR)
                val piloteSpinner = view.findViewById<Spinner>(R.id.DS_PILOT)
                val securitySpinner = view.findViewById<Spinner>(R.id.DS_SECURITY)
                fillList(locationSpinner, locationList)
                fillList(boatSpinner, boatList)
                fillList(levelSpinner, levelList)
                fillList(directorSpinner, director)
                fillList(piloteSpinner, pilote)
                fillList(securitySpinner, security)
                val numberDivers = view.findViewById<EditText>(R.id.DS_MIN_DIVER)
                val maxNumberDivers = view.findViewById<EditText>(R.id.DS_MAX_DIVER)
                numberDivers.setOnFocusChangeListener { _, _ ->
                    if (!checkNumberDivers(
                            numberDivers.text.toString(),
                            maxNumberDivers.text.toString()
                        )
                    ) {
                        numberDivers.error =
                            "The number of divers must be less than the maximum number of divers"
                    } else {
                        maxNumberDivers.error = null

                    }
                }
                maxNumberDivers.setOnFocusChangeListener { _, _ ->
                    if (!checkNumberDivers(
                            numberDivers.text.toString(),
                            maxNumberDivers.text.toString()
                        )
                    ) {
                        maxNumberDivers.error =
                            "The number of divers must be less than the maximum number of divers"
                    } else {
                        numberDivers.error = null
                    }
                }
                val date = view.findViewById<EditText>(R.id.DS_DATE)
                date.setOnFocusChangeListener { _, _ ->
                    if (!checkDate(date.text.toString())) {
                        date.error = "The date must be less than the current date"
                    }
                }
                val button = view.findViewById<Button>(R.id.CREATE)
                button.setOnClickListener {
                    if (checkNumberDivers(
                            numberDivers.text.toString(),
                            maxNumberDivers.text.toString()
                        ) && checkDate(date.text.toString())
                    ) {
                        val data =
                            "DS_DATE=${date.text}&DS_LOCATION=${locationSpinner.selectedItem}&DS_BOAT=${boatSpinner.selectedItem}&DS_LEVEL=${levelSpinner.selectedItem}&DS_DIRECTOR=${directorSpinner.selectedItem}&DS_PILOT=${piloteSpinner.selectedItem}&DS_SECURITY=${securitySpinner.selectedItem}&DS_MIN_DIVER=${numberDivers.text}&DS_MAX_DIVER=${maxNumberDivers.text}"
                        createDive(data)
                    }
                }
                view
            }
        )
    }

    private suspend fun requestToAPIData(url: URL, name: String): List<String> {
        return withContext(Dispatchers.IO) {
            val responseLocation = StringBuffer()
            with(url.openConnection() as HttpsURLConnection) {
                requestMethod = "GET"
                BufferedReader(inputStream.reader()).use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        responseLocation.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    return@withContext parseList(responseLocation.toString(), name)
                }
            }
        }
    }

    private fun parseList(response: String, name: String): List<String> {
        val locationList = mutableListOf<String>()
        var jsonObject = JSONObject()
        try {
            jsonObject = JSONObject(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (i in 0 until jsonObject.getJSONArray("data").length()) {
            locationList.add(jsonObject.getJSONArray("data").getJSONObject(i).getString(name))
        }
        return locationList
    }

    private fun fillList(spinner: Spinner, list: List<String>) {
        val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private suspend fun getUserRole(roles: String): MutableList<String> {
        return withContext(Dispatchers.IO) {
            val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/user")
            val response = StringBuffer()
            with(url.openConnection() as HttpsURLConnection) {
                requestMethod = "GET"
                BufferedReader(inputStream.reader()).use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                }
            }
            val role = getRoleAttribution(roles)
            val user = mutableListOf<String>()
            var json = JSONObject()
            try {
                json = JSONObject(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            for (i in 0 until json.getJSONArray("data").length()) {
                val id = json.getJSONArray("data").getJSONObject(i).getString("US_ID")
                if (role.contains(id)) {
                    user.add(json.getJSONArray("data").getJSONObject(i).getString("US_NAME"))
                }
            }
            return@withContext user
        }
    }

    private suspend fun getRoleAttribution(roles: String): MutableList<String> {
        return withContext(Dispatchers.IO) {
            val dataList = mutableListOf<String>()
            val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/roleattribution")
            val response = StringBuffer()
            with(url.openConnection() as HttpsURLConnection) {
                requestMethod = "GET"
                BufferedReader(inputStream.reader()).use {
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                }
            }
            var json = JSONObject()
            try {
                json = JSONObject(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            for (i in 0 until json.getJSONArray("data").length()) {
                val role = json.getJSONArray("data").getJSONObject(i).getString("ROL_CODE")
                val id = json.getJSONArray("data").getJSONObject(i).getString("US_ID")
                when (roles) {
                    "DIRECTOR" -> {
                        if (role == "DIR") {
                            dataList.add(id)
                        }
                    }

                    "PILOTE" -> {
                        if (role == "PIL") {
                            dataList.add(id)
                        }
                    }

                    "SECURITY" -> {
                        if (role == "SEC") {
                            dataList.add(id)
                        }
                    }
                }
            }
            return@withContext dataList
        }
    }

    private fun checkNumberDivers(numberDivers: String, maxNumberDivers: String): Boolean {
        if (numberDivers.isEmpty() || maxNumberDivers.isEmpty()) {
            return false
        }
        return numberDivers.toInt() <= maxNumberDivers.toInt()
    }

    private fun checkDate(date: String): Boolean {
        if (date.isEmpty()) {
            return false
        }
        if (!date.matches(Regex("([0-9]{2})/([0-9]{2})/([0-9]{4})"))) {
            return false
        }
        return true
    }


    private suspend fun sendData(data: String) {
        return withContext(Dispatchers.IO) {
            val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/createdive")
            with(url.openConnection() as HttpsURLConnection) {
                requestMethod = "POST"
                doOutput = true
                outputStream.write(data.toByteArray(StandardCharsets.UTF_8))
                outputStream.flush()
                if (responseCode == 200) {
                    Log.d("DiveCreation", "Dive created")
                } else {
                    println("Error $responseMessage")
                }
            }
        }
    }


    private fun createDive(data: String) {
        (CoroutineScope(Dispatchers.IO)).launch {
            sendData(data)
        }
    }


}