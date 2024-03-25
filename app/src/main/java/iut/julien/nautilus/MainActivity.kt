package iut.julien.nautilus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import iut.julien.nautilus.ui.ScreenEnum
import iut.julien.nautilus.ui.SettingsDialog
import iut.julien.nautilus.ui.model.DiveListViewModel
import iut.julien.nautilus.ui.theme.NautilusTheme

/**
 * The main activity
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NautilusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NautilusApp()
                }
            }
        }
    }
}

/**
 * The main app
 * @param modifier Modifier for styling
 */
@Composable
fun NautilusApp(modifier: Modifier = Modifier) {
    val diveListViewModel: DiveListViewModel = viewModel()
    diveListViewModel.retrieveDives()
    val navController = rememberNavController()
    val selectedScreen = remember { mutableIntStateOf(0) }
    val displayIDSettings = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            AppTopBar(selectedScreen = selectedScreen)
        },
        bottomBar = {
            AppNavigationBar(
                navController = navController,
                selectedScreen = selectedScreen,
                displayIDSettings = displayIDSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "Dives",
                modifier = modifier
            ) {
                for (screen in ScreenEnum.entries) {
                    composable(screen.routeName) {
                        screen.GetContent(diveListViewModel)
                    }
                }
            }
        }
        if (displayIDSettings.value) {
            SettingsDialog().IDSettingsScreen(displayIDSettings, diveListViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    selectedScreen: MutableIntState
) {
    selectedScreen.intValue = selectedScreen.intValue
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_simple),
                    contentDescription = "App icon",
                    modifier = Modifier.width(48.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    )
}

/**
 * A navigation bar for the app
 * @param navController NavController for navigation
 */
@Composable
fun AppNavigationBar(
    navController: NavController,
    selectedScreen: MutableIntState,
    displayIDSettings: MutableState<Boolean>
) {
    NavigationBar {
        ScreenEnum.entries.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (index == selectedScreen.intValue) item.iconFilled else item.icon,
                        contentDescription = item.contentDescription
                    )
                },
                label = { Text(item.contentDescription) },
                selected = selectedScreen.intValue == index,
                onClick = {
                    navController.navigate(item.routeName)
                    selectedScreen.intValue = index
                }
            )
        }
        NavigationBarItem(
            selected = false,
            onClick = { displayIDSettings.value = !displayIDSettings.value },
            icon = {
                Icon(Icons.Filled.Settings, contentDescription = "Diver ID Settings")
            },
            label = { Text("Settings") })
    }
}