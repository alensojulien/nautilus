package iut.julien.nautilus.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import iut.julien.nautilus.ui.model.DiveListViewModel

/**
 * Enum class that represents the different screens of the app.
 *
 * @param routeName The name of the route
 * @param icon The icon of the screen
 * @param iconFilled The icon of the screen when it is selected
 * @param contentDescription The content description of the screen
 */
enum class ScreenEnum(
    val routeName: String = "",
    val icon: ImageVector,
    val iconFilled: ImageVector,
    val contentDescription: String = ""
) {
    /**
     * The Dives list screen.
     */
    Dives(
        routeName = "Dives",
        icon = Icons.AutoMirrored.Outlined.List,
        iconFilled = Icons.AutoMirrored.Filled.List,
        contentDescription = "Dives"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) =
            Dives().DivesScreen(diveListViewModel)
    },
    /**
     * The Dive creation screen.
     */
    DiveCreation(
        routeName = "Dive Creation",
        icon = Icons.Outlined.Create,
        iconFilled = Icons.Filled.Create,
        contentDescription = "Dive Creation"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) =
            DiveCreation().DiveCreationScreen(diveListViewModel = diveListViewModel)
    };

    /**
     * Function that returns the content of the screen.
     *
     * @param diveListViewModel The view model of the dives list
     */
    @Composable
    @Override
    open fun GetContent(diveListViewModel: DiveListViewModel) =
        Dives().DivesScreen(diveListViewModel)
}