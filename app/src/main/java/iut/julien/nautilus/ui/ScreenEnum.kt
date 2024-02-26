package iut.julien.nautilus.ui

import androidx.compose.runtime.Composable

enum class ScreenEnum(val routeName: String = "") {
    Dives(routeName = "dives") {
        @Composable
        override fun GetContent() = Dives().DivesScreen()
    },
    DiveCreation(routeName = "divecreation") {
        @Composable
        override fun GetContent() = DiveCreation().DiveCreationScreen()
    };

    @Composable
    @Override
    open fun GetContent() {
        return Dives().DivesScreen()
    }
}