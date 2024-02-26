package iut.julien.nautilus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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

    Scaffold(
        bottomBar = {
            AppNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Column (
            modifier = modifier.padding(innerPadding)
        ) {
            NavHost(navController = navController, startDestination = "Dives", modifier = modifier) {
                for(screen in ScreenEnum.entries) {
                    composable(screen.routeName) {
                        screen.GetContent()
                    }
                }
            }
        }
    }
}

/**
 * A navigation bar for the app
 * @param modifier Modifier for styling
 * @param navController NavController for navigation
 */
@Composable
fun AppNavigationBar(modifier: Modifier = Modifier, navController: NavController) {
    val selectedItem = remember { mutableIntStateOf(0) }
    NavigationBar {
        ScreenEnum.entries.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = selectedItem.intValue == index,
                onClick = {
                    navController.navigate(item.routeName) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                    selectedItem.intValue = index
                }
            )
        }
    }
}