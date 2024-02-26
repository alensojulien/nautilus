package iut.julien.nautilus.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

enum class ScreenEnum(
    val routeName: String = "",
    val icon: ImageVector,
    val iconFilled: ImageVector,
    val contentDescription: String = ""
) {
    Dives(
        routeName = "dives",
        icon = Icons.Outlined.List,
        iconFilled = Icons.Filled.List,
        contentDescription = "Dives"
    ) {
        @Composable
        override fun GetContent() = Dives().DivesScreen()
    },
    DiveCreation(
        routeName = "divecreation",
        icon = Icons.Outlined.Create,
        iconFilled = Icons.Filled.Create,
        contentDescription = "Dive Creation"
    ) {
        @Composable
        override fun GetContent() = DiveCreation().DiveCreationScreen()
    };

    @Composable
    @Override
    open fun GetContent() {
        return Dives().DivesScreen()
    }
}