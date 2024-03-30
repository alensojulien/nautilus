package iut.julien.nautilus.ui.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import iut.julien.nautilus.R
import iut.julien.nautilus.ui.model.Dive
import iut.julien.nautilus.ui.model.DiveListViewModel
import iut.julien.nautilus.ui.utils.FileStorage
import kotlinx.coroutines.delay

/**
 * Dives list screen composable
 */
class Dives {

    /**
     * Dives screen composable
     *
     * @param diveListViewModel the view model to retrieve dives
     */
    @Composable
    fun DivesScreen(diveListViewModel: DiveListViewModel) {
        val openDialog = remember { mutableStateOf(true) }

        // Check if the user is online
        if (!isOnline(LocalContext.current) && openDialog.value) {
            InternetConnectionAlertDialog(openDialog)
            return
        }

        var refreshing by remember { mutableStateOf(false) }
        LaunchedEffect(refreshing) {
            if (refreshing) {
                delay(3000)
                refreshing = false
            }
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = refreshing),
            onRefresh = {
                refreshing = true
                diveListViewModel.retrieveDives()
            }
        ) {
            DivesContent(diveListViewModel = diveListViewModel)
        }
    }

    /**
     * Internet connection alert dialog composable
     *
     * @param openDialog the state of the dialog
     */
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

    /**
     * Check if the user is online
     *
     * @param context the context
     * @return true if the user is online, false otherwise
     */
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

    /**
     * Dives content composable
     *
     * @param diveListViewModel the view model to retrieve dives
     */
    @Composable
    fun DivesContent(diveListViewModel: DiveListViewModel) {
        // Retrieve the list of dives
        val divesList by diveListViewModel.divesList.collectAsState(initial = emptyList())
        val expandedCardId = remember { mutableStateOf("") }
        val likedDives = FileStorage.getFavoriteDives(context = LocalContext.current)
        divesList.forEach { likedDive ->
            likedDive.isLiked = likedDives.contains(likedDive.diveId)
        }
        val onlyDisplayLikedDives = remember { mutableStateOf(false) }
        LazyColumn(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxWidth()
        ) {
            // Display the swipe down refresh message
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Arrow down icon")
                    Text(
                        text = stringResource(R.string.swipe_down_refresh_msg),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Arrow down icon")
                }
            }

            // Display the dives list title
            item {
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Dives list", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.padding(8.dp))
            }

            // Display the liked dives filter chip
            item {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp, 0.dp)
                ) {
                    LikedDivesFilterChip(selected = onlyDisplayLikedDives)
                }
            }

            // Display the dive loading progress indicator
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

            var filteredDivesList = divesList
            if (onlyDisplayLikedDives.value) {
                // Display the dives list filtered by liked dives
                filteredDivesList =
                    divesList.filter { it.isLiked }
                if (filteredDivesList.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.no_liked_dives_to_display_msg),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Display the dives list
            items(
                count = filteredDivesList.size,
                key = { filteredDivesList[it].diveId }) { index ->
                DiveCard(
                    diveAttr = filteredDivesList[index],
                    diveListViewModel = diveListViewModel,
                    expandedCardId = expandedCardId,
                    context = LocalContext.current
                )
            }

        }
    }

    /**
     * Liked dives filter chip composable
     *
     * @param selected the state of the chip
     */
    @Composable
    fun LikedDivesFilterChip(selected: MutableState<Boolean>) {
        FilterChip(
            onClick = { selected.value = !selected.value },
            label = {
                Text(stringResource(R.string.liked_dives_filter))
            },
            selected = selected.value,
            leadingIcon = if (selected.value) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            }
        )
    }

    /**
     * Dive card composable
     *
     * @param dive the dive to display
     * @param diveListViewModel the view model to retrieve dives
     * @param expandedCardId the state of the expanded card
     * @param context the context
     */
    @Composable
    fun DiveCard(
        diveAttr: Dive,
        diveListViewModel: DiveListViewModel,
        expandedCardId: MutableState<String>,
        context: Context
    ) {
        val dive = remember { diveAttr }
        val cardExpendedState = remember { mutableStateOf(false) }
        LaunchedEffect(expandedCardId.value) {
            cardExpendedState.value = expandedCardId.value == dive.diveId
        }
        val cardExpandedHeight by animateDpAsState(
            if (cardExpendedState.value) 264.dp else 0.dp,
            label = "Card expanded height animation"
        )
        cardExpandedHeight.plus(300.dp)
        val arrowDownOrientation by animateFloatAsState(
            if (cardExpendedState.value) 180f else 0f,
            label = "Arrow orientation animation"
        )
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val icon = remember {
                            if (dive.isLiked) {
                                mutableStateOf(Icons.Filled.Favorite)
                            } else {
                                mutableStateOf(Icons.Filled.FavoriteBorder)
                            }
                        }
                        IconButton(onClick = {
                            icon.value = if (dive.isLiked) {
                                FileStorage.removeFavoriteDive(
                                    diveID = dive.diveId,
                                    context = context
                                )
                                dive.isLiked = false
                                Icons.Filled.FavoriteBorder
                            } else {
                                FileStorage.addFavoriteDive(diveID = dive.diveId, context = context)
                                dive.isLiked = true
                                Icons.Filled.Favorite
                            }
                        }) {
                            Icon(icon.value, contentDescription = "Heart icon")
                        }
                        IconButton(onClick = {
                            expandedCardId.value = if (cardExpendedState.value) "" else dive.diveId
                        }) {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Arrow down icon",
                                modifier = Modifier.rotate(arrowDownOrientation)
                            )
                        }
                    }
                }
                // Display the expanded card content
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
                        modifier = Modifier
                            .padding(16.dp, 0.dp)
                            .height(64.dp)
                    ) {
                        if (dive.diveDivers.isEmpty()) {
                            item {
                                Text(text = "No divers registered yet")
                            }
                        }
                        items(dive.diveDivers.size) { diverIndex ->
                            val diver = remember { dive.diveDivers[diverIndex] }
                            Text(text = "${diverIndex + 1}. ${diver.diverFirstName} ${diver.diverName.uppercase()}")
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 16.dp, 0.dp, 0.dp)
                    ) {
                        var isRegistered = remember { false }
                        val diverID = remember { diveListViewModel.userID.value }
                        dive.diveDivers.forEach {
                            if (it.diverId == diverID) isRegistered = true
                        }

                        Button(
                            onClick = {
                                diveListViewModel.registerToDive(diveID = dive.diveId)
                                expandedCardId.value = ""
                                Toast.makeText(
                                    context,
                                    "You are now registered to the dive! (${dive.diveLocation} - ${dive.diveDate})",
                                    Toast.LENGTH_LONG
                                ).show()
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
}