package iut.julien.nautilus.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import iut.julien.nautilus.R
import iut.julien.nautilus.ui.model.Dive
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread


class Dives {

    @Composable
    fun DivesScreen() {
        val openDialog = remember { mutableStateOf(true) }

        if (!isOnline(LocalContext.current) && openDialog.value) {
            InternetConnectionAlertDialog(openDialog)
            return
        }

        DivesContent()
    }

    @Composable
    private fun InternetConnectionAlertDialog(openDialog: MutableState<Boolean>) {
        AlertDialog(
            icon = @Composable {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = "No internet connection icon"
                )
            },
            onDismissRequest = { openDialog.value = false },
            confirmButton = { Button(onClick = { openDialog.value = false }) {
                Text(text = "Done")
            }  },
            title = {
                Text(stringResource(id = R.string.no_internet_connection))
            },
            text = {
                Text(stringResource(id = R.string.no_internet_connection_message))
            })
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    @Composable
    fun DivesContent(modifier: Modifier = Modifier) {
        var dives: MutableList<Dive> = mutableListOf()
        thread {
            dives = retrieveDives()
        }
        Column (
            modifier = modifier.padding(16.dp)
        ) {
            Text(text = "Dives list", style = MaterialTheme.typography.headlineMedium)

            for (dive in dives) {
                Text(text = dive.getDiveLocation())
            }
        }
    }

    private fun retrieveDives(): MutableList<Dive> {
        val dives = mutableListOf<Dive>()
        val url: URL = URL("https://dev-sae301grp3.users.info.unicaen.fr/api/dives")
        var response = StringBuffer()
        with(url.openConnection() as HttpsURLConnection) {
            requestMethod = "GET"

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println("Response : $response")
            }
        }
        dives = mutableListOf(Json.decodeFromString(response.toString()))
        return dives
    }
}