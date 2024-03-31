package iut.julien.nautilus.ui.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import iut.julien.nautilus.ui.utils.FileStorage
import iut.julien.nautilus.ui.utils.URLEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.net.ssl.HttpsURLConnection

/**
 * ViewModel for the DiveListFragment.
 */
class DiveListViewModel : ViewModel() {
    /**
     * List of dives retrieved.
     */
    private val _divesList = MutableLiveData<MutableList<Dive>>(
        mutableStateListOf()
    )

    /**
     * User ID.
     */
    var userID = MutableLiveData("1")

    /**
     * Flow of the list of dives.
     */
    val divesList = _divesList.asFlow()

    /**
     * Retrieve the list of dives.
     */
    fun retrieveDives(context: Context) {
        viewModelScope.launch {
            val listOfDives: MutableList<Dive> = mutableStateListOf()
            withContext(Dispatchers.IO) {
                val url = URL(URLEnum.DIVES_LIST.url)
                val responseDives = StringBuffer()
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"

                    BufferedReader(InputStreamReader(inputStream)).use {
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            responseDives.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                    }
                }

                var jsonObject = JSONObject()
                try {
                    jsonObject = JSONObject(responseDives.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                for (i in 0..<jsonObject.getJSONArray("data").length()) {
                    val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val date = LocalDate.parse(
                        jsonObject.getJSONArray("data").getJSONObject(i)
                            .getString("DS_DATE"), dateFormat
                    )
                    val dateFormatted =
                        (if (date.dayOfMonth > 9) date.dayOfMonth.toString() else "0" + date.dayOfMonth.toString()) + "/" + (if (date.monthValue > 9) date.monthValue else "0" + date.monthValue) + "/" +
                                date.year.toString()
                    listOfDives.add(
                        Dive(
                            diveDate = dateFormatted,
                            diveId = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("DS_CODE"),
                            diveDepth = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("DL_DEPTH"),
                            diveLocation = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("DL_NAME"),
                            diveNumberDivers = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("DS_DIVERS_COUNT"),
                            diveMaxNumberDivers = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("DS_MAX_DIVERS"),
                            diveTime = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("CAR_SCHEDULE")
                        )
                    )
                }

                val likedDives = FileStorage.getFavoriteDives(context = context)
                listOfDives.forEach { likedDive ->
                    likedDive.isLiked = likedDives.contains(likedDive.diveId)
                }

                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                listOfDives.sortWith(
                    compareBy(
                        { LocalDate.parse(it.diveDate, formatter) },
                        { it.diveId })
                )
                _divesList.postValue(listOfDives)
                // Allow to retrieve dive participants
                retrieveDivers()
            }
        }
    }

    /**
     * Retrieve the divers for each dive. (US_ID only)
     */
    private fun retrieveDivers() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val url = URL(URLEnum.DIVE_REGISTRATION.url)
                val responseDivers = StringBuffer()
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"

                    BufferedReader(InputStreamReader(inputStream)).use {
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            responseDivers.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                    }
                }

                var jsonObject = JSONObject()
                try {
                    jsonObject = JSONObject(responseDivers.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                for (i in 0..<jsonObject.getJSONArray("data").length()) {
                    for (diveIndex in 0..<_divesList.value?.size!!) {
                        if (_divesList.value?.get(diveIndex)?.diveId == jsonObject.getJSONArray("data")
                                .getJSONObject(i).getString("DS_CODE")
                        )
                            _divesList.value?.get(diveIndex)?.diveDiversID?.add(
                                jsonObject.getJSONArray("data").getJSONObject(i)
                                    .getString("US_ID")
                            )
                    }
                }
                // Makes the link between US_ID and the diver's information
                retrieveDiversInfo()
            }
        }
    }

    /**
     * Retrieve the divers' information. (name, firstname, ...)
     */
    private fun retrieveDiversInfo() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val url = URL(URLEnum.USER_LIST.url)
                val responseDivers = StringBuffer()
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"

                    BufferedReader(InputStreamReader(inputStream)).use {
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            responseDivers.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                    }
                }

                var jsonObject = JSONObject()
                try {
                    jsonObject = JSONObject(responseDivers.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                for (i in 0..<jsonObject.getJSONArray("data").length()) {
                    for (diveIndex in 0..<_divesList.value?.size!!) {
                        for (diverIndex in 0..<_divesList.value?.get(diveIndex)?.diveDiversID?.size!!) {
                            if (_divesList.value?.get(diveIndex)?.diveDiversID?.get(diverIndex) == jsonObject.getJSONArray(
                                    "data"
                                )
                                    .getJSONObject(i).getString("US_ID")
                            ) {
                                _divesList.value?.get(diveIndex)?.diveDivers?.add(
                                    Diver(
                                        diverId = jsonObject.getJSONArray("data").getJSONObject(i)
                                            .getString("US_ID"),
                                        diverPreCode = jsonObject.getJSONArray("data")
                                            .getJSONObject(i)
                                            .getString("PRE_CODE"),
                                        diverFirstName = jsonObject.getJSONArray("data")
                                            .getJSONObject(i)
                                            .getString("US_FIRST_NAME"),
                                        diverName = jsonObject.getJSONArray("data").getJSONObject(i)
                                            .getString("US_NAME")
                                    )
                                )
                            }
                        }
                    }
                }
                _divesList.postValue(_divesList.value)
            }
        }
    }

    /**
     * Register to a dive.
     *
     * @param diveID Dive ID.
     */
    fun registerToDive(diveID: String, context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val url = URL(
                    "${URLEnum.DIVE_SUBSCRIBE.url}?US_ID=${userID.value}&DS_CODE=${diveID}"
                )
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    outputStream.flush()
                    println("Post response code : $responseCode")
                }
                println(url)
            }
            println("Registered to dive $diveID")
            retrieveDives(context = context)
        }
    }

    /**
     * User list
     */
    private val _userList = MutableLiveData<MutableList<DatabaseObject>>(
        mutableStateListOf()
    )

    /**
     * Flow of the list of users.
     */
    val userList = _userList.asFlow()

    /**
     * Retrieve the list of all users.
     */
    fun retrieveAllDivers() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val url = URL(URLEnum.USER_LIST.url)
                val responseDivers = StringBuffer()
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"

                    BufferedReader(InputStreamReader(inputStream)).use {
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            responseDivers.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                    }
                }

                var jsonObject = JSONObject()
                try {
                    jsonObject = JSONObject(responseDivers.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                _userList.value?.clear()
                for (i in 0..<jsonObject.getJSONArray("data").length()) {
                    _userList.value?.add(
                        DatabaseObject(
                            id = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("US_ID"),
                            name = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("US_FIRST_NAME") + " " + jsonObject.getJSONArray("data")
                                .getJSONObject(i).getString("US_NAME").uppercase()
                        )
                    )
                }
                _userList.postValue(_userList.value)
            }
        }
    }
}