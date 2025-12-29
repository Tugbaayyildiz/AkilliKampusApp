

package com.example.akillikampusapp.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapPickerScreen(
    onLocationSelected: (Double, Double) -> Unit,
    onBack: () -> Unit
) {
    // Başlangıç: Ankara (sabit, sadece açılış için)

    val startLocation = LatLng(39.9049, 41.2670)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 20f)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                mapToolbarEnabled = true
            ),
            onMapClick = { latLng ->
                selectedLatLng = latLng
            }
        )
        {
            selectedLatLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Seçilen Konum"
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {

            selectedLatLng?.let {
                Text(
                    text = "Seçilen Konum: ${it.latitude}, ${it.longitude}"
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    selectedLatLng?.let {
                        onLocationSelected(it.latitude, it.longitude)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedLatLng != null
            ) {
                Text("Konumu Seç")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("İptal")
            }
        }
    }
}
// first small change
