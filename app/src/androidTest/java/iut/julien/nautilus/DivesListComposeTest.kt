package iut.julien.nautilus

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import iut.julien.nautilus.ui.theme.NautilusTheme
import org.junit.Rule
import org.junit.Test

class DivesListComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun divesList() {
        composeTestRule.setContent {
            NautilusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivity().NautilusApp()
                }
            }
        }

        composeTestRule.onNodeWithText("Nautilus").assertIsDisplayed()
        composeTestRule.onNodeWithText("Swipe down to refresh").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dives list").assertIsDisplayed()

        composeTestRule.onNodeWithText("Display only liked dives").assertIsDisplayed()
        composeTestRule.onNodeWithText("No liked dives to display").assertIsNotDisplayed()
        composeTestRule.onNodeWithText("Display only liked dives").performClick()
        composeTestRule.onNodeWithText("No liked dives to display").assertIsDisplayed()
    }
}