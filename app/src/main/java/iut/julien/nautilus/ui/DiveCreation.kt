package iut.julien.nautilus.ui

import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import iut.julien.nautilus.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import org.json.JSONObject

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
        LaunchedEffect(Unit) {
            val list = requestToAPIData(URL("https://dev-sae301grp3.users.info.unicaen.fr/api/divinglocation"), "DL_NAME")
            locationList.addAll(list)
            val listBoat = requestToAPIData(URL("https://dev-sae301grp3.users.info.unicaen.fr/api/boat"), "BO_NAME")
            boatList.addAll(listBoat)
            val listLevel = requestToAPIData(URL("https://dev-sae301grp3.users.info.unicaen.fr/api/prerogative"), "PRE_CODE")
            levelList.addAll(listLevel)
        }
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context).inflate(R.layout.activity_main, null)
                val locationSpinner = view.findViewById<Spinner>(R.id.DS_LOCATION)
                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, locationList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                locationSpinner.adapter = adapter
                val boatSpinner = view.findViewById<Spinner>(R.id.DS_BOAT)
                val adapterBoat = ArrayAdapter(context, android.R.layout.simple_spinner_item, boatList)
                adapterBoat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                boatSpinner.adapter = adapterBoat
                val levelSpinner = view.findViewById<Spinner>(R.id.DS_LEVEL)
                val adapterLevel = ArrayAdapter(context, android.R.layout.simple_spinner_item, levelList)
                adapterLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                levelSpinner.adapter = adapterLevel
                view
            }
        )
    }

    suspend fun requestToAPIData(url: URL, name: String): List<String>{
        return withContext(Dispatchers.IO){
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
                    val location = parseList(responseLocation.toString(), name)
                    return@withContext location
                }
            }

        }
    }

    private fun parseList(response: String, name: String): List<String> {
        var locationList = mutableListOf<String>()
        var jsonObject = JSONObject()
        try{
            jsonObject = JSONObject(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        for(i in 0 until jsonObject.getJSONArray("data").length()) {
            locationList.add(jsonObject.getJSONArray("data").getJSONObject(i).getString(name))
        }
        return locationList
    }

}