package iut.julien.nautilus.ui.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
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

class DiveListViewModel : ViewModel() {
    private val _divesList = MutableLiveData<MutableList<Dive>>(
        mutableStateListOf()
    )

    var userID = MutableLiveData("1")

    val divesList = _divesList.asFlow()

    fun retrieveDives() {
        viewModelScope.launch {
            val listOfDives: MutableList<Dive> = mutableStateListOf()
            withContext(Dispatchers.IO) {
                val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/dives")
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
                _divesList.postValue(listOfDives)
            }
        }
    }

    fun retrieveDivers(diveIndex: Int) {
        viewModelScope.launch {
            val listOfDiversId: MutableList<String> = mutableStateListOf()
            withContext(Dispatchers.IO) {
                val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/registration")
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
                    if (jsonObject.getJSONArray("data").getJSONObject(i).getString("DS_CODE") == _divesList.value?.get(diveIndex)?.diveId) {
                        listOfDiversId.add(
                            jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("US_ID")
                        )
                    }
                }
                retrieveDiversInfos(listOfDiversId, diveIndex)
            }
        }
    }

    private fun retrieveDiversInfos(listOfDiversId: MutableList<String>, diveIndex: Int) {
        viewModelScope.launch {
            val listOfDivers: MutableList<Diver> = mutableStateListOf()
            withContext(Dispatchers.IO) {
                val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/user")
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
                    if (listOfDiversId.contains(jsonObject.getJSONArray("data").getJSONObject(i).getString("US_ID"))) {
                        listOfDivers.add(
                            Diver(
                                diverId = jsonObject.getJSONArray("data").getJSONObject(i)
                                    .getString("US_ID"),
                                diverPreCode = jsonObject.getJSONArray("data").getJSONObject(i)
                                    .getString("PRE_CODE"),
                                diverFirstName = jsonObject.getJSONArray("data").getJSONObject(i)
                                    .getString("US_FIRST_NAME"),
                                diverName = jsonObject.getJSONArray("data").getJSONObject(i)
                                    .getString("US_NAME")
                            )
                        )
                    }
                }
                _divesList.value?.get(diveIndex)?.diveDivers = listOfDivers
                _divesList.postValue(_divesList.value)
                println(listOfDivers.getOrNull(0) ?: "No diver")
            }
        }
    }

    fun registerToDive(diveIndex: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/subscribe?US_ID=${userID.value}&DS_CODE=${_divesList.value?.get(diveIndex)?.diveId}")
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "POST"
                    doOutput = true
                    outputStream.flush()
                    println("Post response code : $responseCode")
                }
            }
            retrieveDives()
        }
    }
}