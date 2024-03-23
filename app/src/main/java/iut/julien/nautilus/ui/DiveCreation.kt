package iut.julien.nautilus.ui

import android.view.LayoutInflater
import android.widget.Spinner
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import iut.julien.nautilus.R

class DiveCreation {
    @Composable
    fun DiveCreationScreen() {
        AndroidView(
            factory = { context ->
                val view = LayoutInflater.from(context).inflate(R.layout.activity_main, null)
                val locationSpinner = view.findViewById<Spinner>(R.id.DS_LOCATION)

                view
            }
        )
    }

    fun requestToAccessLocationList(){
        
    }
}