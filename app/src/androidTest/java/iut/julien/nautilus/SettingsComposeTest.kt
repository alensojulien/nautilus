package iut.julien.nautilus

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import iut.julien.nautilus.ui.theme.NautilusTheme
import org.junit.Rule
import org.junit.Test

class SettingsComposeTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsTest() {
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
        composeTestRule.onNodeWithText("Settings").performClick()
        composeTestRule.onNodeWithText("Select a user").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select a user").performClick()
        composeTestRule.onNodeWithText("Guilhem SAINT-GAUDIN").assertIsDisplayed()

    }
}