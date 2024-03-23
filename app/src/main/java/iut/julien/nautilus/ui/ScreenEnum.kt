package iut.julien.nautilus.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import iut.julien.nautilus.ui.model.DiveListViewModel

enum class ScreenEnum(
    val routeName: String = "",
    val icon: ImageVector,
    val iconFilled: ImageVector,
    val contentDescription: String = ""
) {
    Dives(
        routeName = "Dives",
        icon = Icons.Outlined.List,
        iconFilled = Icons.Filled.List,
        contentDescription = "Dives"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) = Dives().DivesScreen(diveListViewModel)
    },
    DiveCreation(
        routeName = "Dive Creation",
        icon = Icons.Outlined.Create,
        iconFilled = Icons.Filled.Create,
        contentDescription = "Dive Creation"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) = DiveCreation().DiveCreationScreen()
    };

    @Composable
    @Override
    open fun GetContent(diveListViewModel: DiveListViewModel) {
        return Dives().DivesScreen(diveListViewModel)
    }
}