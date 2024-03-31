package iut.julien.nautilus.ui.screen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import iut.julien.nautilus.R
import iut.julien.nautilus.ui.model.DatabaseObject
import iut.julien.nautilus.ui.model.DiveListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject

/**
 * DiveCreation class used to display the dive creation screen
 */
class DiveCreation {
    /**
     * DiveListViewModel used to retrieve the dives
     */
    private var diveListViewModel: DiveListViewModel = DiveListViewModel()

    /**
     * DiveCreationScreen used to display the dive creation screen
     *
     * @param diveListViewModel DiveListViewModel used to retrieve the dives
     */
    @Composable
    fun DiveCreationScreen(diveListViewModel: DiveListViewModel) {
        this.diveListViewModel = diveListViewModel
        val locationList = remember {
            mutableStateListOf(DatabaseObject())
        }
        val boatList = remember {
            mutableStateListOf(DatabaseObject())
        }
        val levelList = remember {
            mutableStateListOf(DatabaseObject())
        }
        val director = remember {
            mutableStateListOf(DatabaseObject())
        }
        val security = remember {
            mutableStateListOf(DatabaseObject())
        }
        val pilot = remember {
            mutableStateListOf(DatabaseObject())
        }
        LaunchedEffect(Unit) {
            val list = requestToAPIData(
                url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/divinglocation"),
                id = "DL_ID",
                name = "DL_NAME"
            )
            locationList.addAll(list)
            val listBoat = requestToAPIData(
                url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/boat"),
                id = "BO_ID",
                name = "BO_NAME"
            )
            boatList.addAll(listBoat)
            val listLevel = requestToAPIData(
                URL("https://dev-sae301grp3.users.info.unicaen.fr/api/prerogative"),
                id = "PRE_CODE",
                name = "PRE_CODE"
            )
            levelList.addAll(listLevel)
            val listDirector = getUserRole("DIRECTOR")
            director.addAll(listDirector)
            val listPilot = getUserRole("PILOTE")
            pilot.addAll(listPilot)
            val listSecurity = getUserRole("SECURITY")
            security.addAll(listSecurity)
        }
        // Display the view dive creation screen
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val view = LayoutInflater.from(context).inflate(R.layout.activity_create_dive, null)
                val startTimeSpinner = view.findViewById<Spinner>(R.id.DS_START_TIME)
                val locationSpinner = view.findViewById<Spinner>(R.id.DS_LOCATION)
                val boatSpinner = view.findViewById<Spinner>(R.id.DS_BOAT)
                val levelSpinner = view.findViewById<Spinner>(R.id.DS_LEVEL)
                val directorSpinner = view.findViewById<Spinner>(R.id.DS_DIRECTOR)
                val pilotSpinner = view.findViewById<Spinner>(R.id.DS_PILOT)
                val securitySpinner = view.findViewById<Spinner>(R.id.DS_SECURITY)
                fillList(locationSpinner, locationList)
                fillList(boatSpinner, boatList)
                fillList(levelSpinner, levelList)
                fillList(directorSpinner, director)
                fillList(pilotSpinner, pilot)
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
                        date.error = "The date entered is invalid"
                    }
                }
                val button = view.findViewById<Button>(R.id.CREATE)
                button.setOnClickListener {
                    if (!checkNumberDivers(
                            numberDivers.text.toString(),
                            maxNumberDivers.text.toString()
                        ) || !checkDate(date.text.toString())
                    ) {
                        return@setOnClickListener
                    }

                    val dlId = getSelectedID(list = locationList, spinner = locationSpinner)
                    val boat = getSelectedID(list = boatList, spinner = boatSpinner)
                    val dsDirector = getSelectedID(list = director, spinner = directorSpinner)
                    val dsPilot = getSelectedID(list = pilot, spinner = pilotSpinner)
                    val dsSecurity = getSelectedID(list = security, spinner = securitySpinner)
                    val formattedDate = date.text.toString().split("/").reversed().joinToString("/")

                    val data =
                        "DS_DATE=${formattedDate}&DS_START_TIME=${startTimeSpinner.selectedItem}&LOCATION=${dlId}&BOAT=${boat}&DS_LEVEL=${levelSpinner.selectedItem}&DS_DIRECTOR=${dsDirector}&DS_PILOT=${dsPilot}&DS_SECURITY=${dsSecurity}&DS_MIN_DIVER=${numberDivers.text}&DS_MAX_DIVER=${maxNumberDivers.text}"
                    createDive(data = data, context = context)
                }
                view
            }
        )
    }

    /**
     * getSelectedID used to get the selected ID in a dropdown menu
     *
     * @param list List of DatabaseObject used to get the ID
     * @param spinner Spinner used to get the selected item
     * @return String
     */
    private fun getSelectedID(list: SnapshotStateList<DatabaseObject>, spinner: Spinner): String {
        for (item in list) {
            if (item.name == spinner.selectedItem.toString()) {
                return item.id
            }
        }
        return ""
    }

    /**
     * requestToAPIData used to request data from the API
     *
     * @param url URL used to request the data
     * @param id ID used to get the ID from the JSON response
     * @param name Name used to get the name of the object from the JSON response
     * @return List of DatabaseObject
     */
    private suspend fun requestToAPIData(url: URL, id: String, name: String): List<DatabaseObject> {
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
                    return@withContext parseList(
                        response = responseLocation.toString(),
                        id = id,
                        name = name
                    )
                }
            }
        }
    }

    /**
     * parseList used to parse the JSON response
     *
     * @param response JSON response
     * @param id ID used to get the ID from the JSON response
     * @param name Name used to get the name of the object from the JSON response
     * @return List of DatabaseObject
     */
    private fun parseList(response: String, id: String, name: String): List<DatabaseObject> {
        val locationList: MutableList<DatabaseObject> = mutableListOf()
        var jsonObject = JSONObject()
        try {
            jsonObject = JSONObject(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for (i in 0 until jsonObject.getJSONArray("data").length()) {

            locationList.add(
                DatabaseObject(
                    id = jsonObject.getJSONArray("data").getJSONObject(i).getString(id),
                    name = jsonObject.getJSONArray("data").getJSONObject(i).getString(name)
                )
            )
        }
        return locationList
    }

    /**
     * fillList used to fill a dropdown menu with a list of objects
     *
     * @param spinner Spinner used to fill the dropdown menu
     * @param list List of DatabaseObject used to fill the dropdown menu
     */
    private fun fillList(spinner: Spinner, list: List<DatabaseObject>) {
        val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    /**
     * getUserRole used to get the user role
     *
     * @param roles Roles used to get the user role
     * @return MutableList of DatabaseObject
     */
    private suspend fun getUserRole(roles: String): MutableList<DatabaseObject> {
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
            val user: MutableList<DatabaseObject> = mutableListOf()
            var json = JSONObject()
            try {
                json = JSONObject(response.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            for (i in 0 until json.getJSONArray("data").length()) {
                val id = json.getJSONArray("data").getJSONObject(i).getString("US_ID")
                if (role.contains(id)) {
                    user.add(
                        DatabaseObject(
                            id = id,
                            name = json.getJSONArray("data").getJSONObject(i)
                                .getString("US_FIRST_NAME") + " " + json.getJSONArray("data")
                                .getJSONObject(i).getString("US_NAME").uppercase()
                        )
                    )

                }
            }
            return@withContext user
        }
    }

    /**
     * getRoleAttribution used to get the role attribution
     *
     * @param roles Roles used to get the role attribution
     * @return MutableList of String
     */
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

    /**
     * checkNumberDivers used to check if the number of divers is less than the maximum number of divers
     *
     * @param numberDivers Number of divers
     * @param maxNumberDivers Maximum number of divers
     * @return Boolean
     */
    private fun checkNumberDivers(numberDivers: String, maxNumberDivers: String): Boolean {
        if (numberDivers.isEmpty() || maxNumberDivers.isEmpty()) {
            return false
        }
        return numberDivers.toInt() <= maxNumberDivers.toInt()
    }

    /**
     * checkDate used to check if the date is valid
     *
     * @param date Date
     * @return Boolean
     */
    private fun checkDate(date: String): Boolean {
        if (date.isEmpty()) {
            return false
        }
        if (!date.matches(Regex("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/[0-9]{2}\$"))) {
            return false
        }
        return true
    }

    /**
     * postDiveRequest used to post a dive request
     *
     * @param data Data used to create the dive
     */
    private suspend fun postDiveRequest(data: String, context: Context) {
        return withContext(Dispatchers.IO) {
            val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/createdive?$data")
            with(url.openConnection() as HttpsURLConnection) {
                requestMethod = "POST"
                if (responseCode == 200) {
                    Log.d("DiveCreation", "Dive created")
                    diveListViewModel.retrieveDives(context = context)
                } else {
                    Log.d("DiveCreation", "Dive not created")
                }
            }
        }
    }

    /**
     * createDive used to create a dive
     *
     * @param data Data used to create the dive
     */
    private fun createDive(data: String, context: Context) {
        (CoroutineScope(Dispatchers.IO)).launch {
            postDiveRequest(data = data, context = context)
        }
    }
}