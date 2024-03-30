package iut.julien.nautilus.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import iut.julien.nautilus.ui.model.DatabaseObject
import iut.julien.nautilus.ui.model.DiveListViewModel

/**
 * SettingsDialog is a class that contains the IDSettingsScreen composable function.
 * This function is used to display a dialog that allows the user to change their user ID.
 */
class Settings {

    /**
     * IDSettingsScreen is a composable function that displays a dialog that allows the user to change their user ID.
     *
     * @param diveListViewModel The DiveListViewModel that contains the user ID.
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen(
        diveListViewModel: DiveListViewModel
    ) {
        diveListViewModel.retrieveAllDivers()
        val userList by diveListViewModel.userList.collectAsState(initial = emptyList())
        var userID by remember { mutableStateOf(diveListViewModel.userID.value ?: "1") }
        var expandedDropdown by remember { mutableStateOf(false) }
        var selectedOptionText by remember {
            mutableStateOf(
                // Search for the user with the current ID
                userList.find { it.id == userID } ?:
                DatabaseObject(
                    id = "1",
                    name = "Please select a user"
                )
            )
        }
        LaunchedEffect(userList) {
            if (userList.isNotEmpty()) {
                selectedOptionText = userList.find { it.id == userID } ?: userList.first()
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Settings", style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = {
                    expandedDropdown = !expandedDropdown
                }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedOptionText.name,
                    onValueChange = { },
                    label = { Text("User") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedDropdown
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = {
                        expandedDropdown = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    userList.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(text = user.name) },
                            onClick = {
                                selectedOptionText = user
                                userID = user.id
                                println("User ID: $userID")
                                diveListViewModel.userID.value = userID
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }
}