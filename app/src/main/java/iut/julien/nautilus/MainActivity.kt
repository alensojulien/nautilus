package iut.julien.nautilus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import iut.julien.nautilus.ui.ScreenEnum
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
    val navController = rememberNavController()
    val selectedScreen = remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                modifier = modifier,
                selectedScreen = selectedScreen
            )
        },
        bottomBar = {
            AppNavigationBar(
                navController = navController,
                modifier = modifier,
                selectedScreen = selectedScreen
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
                        screen.GetContent()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier,
    navController: NavHostController,
    selectedScreen: MutableIntState
) {
    selectedScreen.intValue = selectedScreen.intValue
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            var text = stringResource(id = R.string.app_name)
            ScreenEnum.entries.forEach { screen -> run { if (screen.routeName == navController.currentDestination?.route) text += " - " + screen.routeName } }
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            println(navController.previousBackStackEntry?.destination?.route)
            if (navController.previousBackStackEntry?.destination?.route != null) {
                IconButton(onClick = {
                    navController.popBackStack()
                    selectedScreen.intValue = selectedScreen.intValue
                }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    )
}

/**
 * A navigation bar for the app
 * @param modifier Modifier for styling
 * @param navController NavController for navigation
 */
@Composable
fun AppNavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    selectedScreen: MutableIntState
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
                    /*{
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }*/
                    selectedScreen.intValue = index
                }
            )
        }
    }
}