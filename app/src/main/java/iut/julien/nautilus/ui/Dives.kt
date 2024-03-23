package iut.julien.nautilus.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import iut.julien.nautilus.R
import iut.julien.nautilus.ui.model.Dive
import iut.julien.nautilus.ui.model.DiveListViewModel

class Dives {

    @Composable
    fun DivesScreen(diveListViewModel: DiveListViewModel) {
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
    fun DivesContent(diveListViewModel: DiveListViewModel) {
        val divesList by diveListViewModel.divesList.collectAsState(initial = emptyList())
        LazyColumn(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxWidth()
        ) {
            item {
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Dives list", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.padding(8.dp))
            }
            if (divesList.isEmpty()) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                        )
                    }
                }
            }
            items(divesList.size) { index ->
                DiveCard(
                    dive = divesList[index],
                    diveIndex = index,
                    diveListViewModel = diveListViewModel
                )
            }
        }
    }
}

@Composable
fun DiveCard(dive: Dive, diveIndex: Int, diveListViewModel: DiveListViewModel) {
    val cardExpendedState = remember { mutableStateOf(false) }
    val cardExpandedHeight by animateDpAsState(
        if (cardExpendedState.value) 250.dp else 0.dp,
        label = "Card expanded height animation"
    )
    val arrowDownOrientation by animateFloatAsState(
        if (cardExpendedState.value) 180f else 0f,
        label = "Arrow orientation animation"
    )
    if (cardExpendedState.value) {
        println("Dive index: $diveIndex")
        diveListViewModel.retrieveDivers(diveIndex = diveIndex)
    }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .background(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            .fillMaxWidth()
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.LocationOn, contentDescription = "Location icon")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = dive.diveLocation,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.DateRange, contentDescription = "Date icon")
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = dive.diveDate,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                IconButton(onClick = { cardExpendedState.value = !cardExpendedState.value }) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Arrow down icon",
                        modifier = Modifier.rotate(arrowDownOrientation)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxWidth()
                    .height(cardExpandedHeight)
            ) {
                HorizontalDivider()
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Dive depth: ${dive.diveDepth}m",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Number of divers: ${dive.diveNumberDivers}/${dive.diveMaxNumberDivers}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(16.dp, 0.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List icon")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Divers list", style = MaterialTheme.typography.headlineSmall)
                }
                LazyColumn(
                    modifier = Modifier.padding(16.dp, 0.dp)
                ) {
                    if (dive.diveDivers.isEmpty()) {
                        item {
                            LinearProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                trackColor = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier
                                    .padding(10.dp, 10.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                    items(dive.diveDivers.size) { diverIndex ->
                        val diver = dive.diveDivers[diverIndex]
                        Text(text = "${diver.diverFirstName} ${diver.diverName}")
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 16.dp, 0.dp, 0.dp)
                ) {
                    var isRegistered = false
                    dive.diveDivers.forEach {
                        if (it.diverId == diveListViewModel.userID.value) isRegistered = true
                    }

                    Button(
                        onClick = {
                            diveListViewModel.registerToDive(diveIndex = diveIndex)
                        },
                        enabled = !isRegistered
                    ) {
                        Text(text = "Register to this dive")
                    }
                }
            }
        }
    }
}