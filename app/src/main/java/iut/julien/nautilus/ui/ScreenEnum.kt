package iut.julien.nautilus.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Create
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
        icon = Icons.AutoMirrored.Outlined.List,
        iconFilled = Icons.AutoMirrored.Filled.List,
        contentDescription = "Dives"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) =
            Dives().DivesScreen(diveListViewModel)
    },
    DiveCreation(
        routeName = "Dive Creation",
        icon = Icons.Outlined.Create,
        iconFilled = Icons.Filled.Create,
        contentDescription = "Dive Creation"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) =
            DiveCreation().DiveCreationScreen(diveListViewModel = diveListViewModel)
    },
    DiveLiked(
        routeName = "Dive Liked",
        icon = Icons.Filled.FavoriteBorder,
        iconFilled = Icons.Filled.Favorite,
        contentDescription = "Dive Liked"
    ) {
        @Composable
        override fun GetContent(diveListViewModel: DiveListViewModel) =
            DiveLiked().DiveLikedScreen(diveListViewModel)
    };

    @Composable
    @Override
    open fun GetContent(diveListViewModel: DiveListViewModel) {
        return Dives().DivesScreen(diveListViewModel)
    }
}