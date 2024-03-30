package iut.julien.nautilus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import iut.julien.nautilus.ui.model.DiveListViewModel

/**
 * SettingsDialog is a class that contains the IDSettingsScreen composable function.
 * This function is used to display a dialog that allows the user to change their user ID.
 */
class SettingsDialog {

    /**
     * IDSettingsScreen is a composable function that displays a dialog that allows the user to change their user ID.
     * @param displayIDSettings A MutableState that represents whether the dialog should be displayed or not.
     * @param diveListViewModel The DiveListViewModel that contains the user ID.
     */
    @Composable
    fun IDSettingsScreen(
        displayIDSettings: MutableState<Boolean>, diveListViewModel: DiveListViewModel
    ) {
        val userID = remember { mutableStateOf(diveListViewModel.userID.value ?: "") }
        val pattern = Regex("^(\\s*|\\d+)\$")
        Dialog(onDismissRequest = { displayIDSettings.value = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)

                ) {
                    Text(
                        text = "Settings", style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = userID.value,
                        onValueChange = { if (it.matches(pattern)) userID.value = it },
                        label = {
                            Text(
                                text = "User ID",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Justify
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            displayIDSettings.value = false
                            diveListViewModel.userID.value = userID.value
                        }) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}