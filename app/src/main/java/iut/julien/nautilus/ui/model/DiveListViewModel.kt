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
import javax.net.ssl.HttpsURLConnection

class DiveListViewModel : ViewModel() {
    private val _divesList = MutableLiveData<MutableList<Dive>>(
        mutableStateListOf()
    )
    val divesList = _divesList.asFlow()

    fun retrieveDives() {
        viewModelScope.launch {
            val listOfDives: MutableList<Dive> = mutableStateListOf()
            withContext(Dispatchers.IO) {
                val url = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/dives")
                val response = StringBuffer()
                with(url.openConnection() as HttpsURLConnection) {
                    requestMethod = "GET"

                    BufferedReader(InputStreamReader(inputStream)).use {
                        var inputLine = it.readLine()
                        while (inputLine != null) {
                            response.append(inputLine)
                            inputLine = it.readLine()
                        }
                        it.close()
                    }
                }

                val jsonObject = JSONObject(response.toString())
                for (i in 0..<jsonObject.getJSONArray("data").length()) {
                    listOfDives.add(
                        Dive(
                            diveDate = jsonObject.getJSONArray("data").getJSONObject(i)
                                .getString("DS_DATE"),
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
}