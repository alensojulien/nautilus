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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import iut.julien.nautilus.R
import iut.julien.nautilus.ui.model.Dive
import iut.julien.nautilus.ui.model.DiveListViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class Dives {

    @Composable
    fun DivesScreen() {
        val diveListViewModel: DiveListViewModel = viewModel()
        val openDialog = remember { mutableStateOf(true) }

        if (!isOnline(LocalContext.current) && openDialog.value) {
            InternetConnectionAlertDialog(openDialog)
            return
        }

        diveListViewModel.retrieveDives()
        DivesContent(diveListViewModel = diveListViewModel)
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
            confirmButton = {
                Button(onClick = { openDialog.value = false }) {
                    Text(text = "Done")
                }
            },
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
    fun DivesContent(diveListViewModel: DiveListViewModel, modifier: Modifier = Modifier) {
        val divesList by diveListViewModel.divesList.collectAsState(initial = emptyList())

        Column(
            modifier = modifier.padding(16.dp)
        ) {
            Text(text = "Dives list", style = MaterialTheme.typography.headlineMedium)
            divesList?.forEach { dive ->
                Text(text = dive.diveLocation)
            }
        }
    }
}