import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState

@Composable
@Preview
fun ApplicationScope.PaperbackApplication() {
    var status by remember { mutableStateOf(ConnectionStatus.DISCONNECTED) }
    val manager = BluetoothManager { status = it }

    Window(
        state = WindowState(size = DpSize(320.dp, 640.dp)),
        title = "Paperback", onCloseRequest = ::exitApplication, resizable = false
    ) {
        MaterialTheme {
            when (status) {
                ConnectionStatus.CONNECTED -> UploadView(manager)
                else -> ConnectView(manager, status)
            }
        }
    }
}

@Composable
fun ConnectView(
    manager: BluetoothManager,
    status: ConnectionStatus
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Text("Please connect in order to modify or upload photos", textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(8.dp))
        Row {
            BluetoothActionButton(status) { handleBluetoothActionButton(manager, status) }
        }
    }
}

private fun handleBluetoothActionButton(
    manager: BluetoothManager,
    status: ConnectionStatus
) {
    when (status) {
        ConnectionStatus.DISCONNECTED ->
            manager.startScan()
        ConnectionStatus.SCANNING ->
            manager.stopScan()
        ConnectionStatus.CONNECTING ->
            manager.shutdown()
        else ->
            throw IllegalStateException()
    }
}

@Composable
fun BluetoothActionButton(
    status: ConnectionStatus,
    onClick: () -> Unit
) {
    Button(onClick) {
        when (status) {
            ConnectionStatus.DISCONNECTED ->
                Text("Connect")
            ConnectionStatus.SCANNING, ConnectionStatus.CONNECTING ->
                Text("Cancel")
            else ->
                throw IllegalStateException()
        }
    }
}

@Composable
fun UploadView(
    manager: BluetoothManager
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row {
            Text("Click to upload a new photo", textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("No photo chosen")
            Spacer(Modifier.width(4.dp))
            ChooseFileButton {}
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            UploadButton(manager) {}
        }
    }
}

@Composable
fun ChooseFileButton(
    onClick: () -> Unit
) {
    Button(onClick) {
        Text("Choose")
    }
}

@Composable
fun UploadButton(
    manager: BluetoothManager,
    onClick: () -> Unit
) {
    Button(onClick) {
        Text("Upload")
    }
}