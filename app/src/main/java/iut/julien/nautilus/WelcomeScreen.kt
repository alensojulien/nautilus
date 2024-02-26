package iut.julien.nautilus

import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

class WelcomeScreen {
    @Composable
    fun MainScreen() {
        Image(
            painter = painterResource(id = R.drawable.logo_full),
            contentDescription = stringResource(
                R.string.welcome_logo_desc
            )
        )
        Text(text = stringResource(R.string.welcome_slogan))
        Text(text = stringResource(R.string.welcome_description))
        Button(onClick = { /*TODO*/ }) {
            Text(text = stringResource(R.string.welcome_connect_btn))
        }
    }
}