package iut.julien.nautilus.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import iut.julien.nautilus.ui.model.DiveListViewModel

class DiveLiked {
    @Composable
    fun DiveLikedScreen(diveListViewModel: DiveListViewModel) {
        Text(text = "Dive Liked")
    }
}