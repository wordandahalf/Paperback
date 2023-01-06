import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bluetooth.DeviceManager
import bluetooth.DeviceStatus
import image.Image
import org.jetbrains.skiko.loadBytesFromPath
import java.awt.FileDialog
import java.nio.file.Path

@Composable
@Preview
fun ApplicationScope.PaperbackApplication() {
    var status by remember { mutableStateOf(DeviceStatus.DISCONNECTED) }
    var image by remember { mutableStateOf<Path?>(null) }

    val manager = DeviceManager { status = it }

    Window(
        state = WindowState(size = DpSize(320.dp, 640.dp)),
        title = "Paperback", onCloseRequest = ::exitApplication, resizable = false
    ) {
        MaterialTheme {
            when (status) {
                DeviceStatus.CONNECTED ->
                    UploadView(window, manager, image) { image = it }
                else -> ConnectView(manager, status)
            }
        }
    }
}

@Composable
fun ConnectView(
    manager: DeviceManager,
    status: DeviceStatus
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
    manager: DeviceManager,
    status: DeviceStatus
) {
    when (status) {
        DeviceStatus.DISCONNECTED ->
            manager.startScan()
        DeviceStatus.SCANNING ->
            manager.stopScan()
        DeviceStatus.CONNECTING ->
            manager.shutdown()
        else ->
            throw IllegalStateException()
    }
}

@Composable
fun BluetoothActionButton(
    status: DeviceStatus,
    onClick: () -> Unit
) {
    Button(onClick) {
        when (status) {
            DeviceStatus.DISCONNECTED ->
                Text("Connect")
            DeviceStatus.SCANNING ->
                Text("Scanning...")
            DeviceStatus.CONNECTING ->
                Text("Connecting...")
            else ->
                throw IllegalStateException()
        }
    }
}

@Composable
fun UploadView(
    window: ComposeWindow,
    manager: DeviceManager,
    image:   Path?,
    onImageSelect: (Path) -> Unit
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
            Text(image?.fileName?.toString() ?: "No photo chosen")
            Spacer(Modifier.width(4.dp))
            ChooseFileButton {
                val dialog = FileDialog(window).also {
                    it.setFilenameFilter { _, name ->
                        listOf("png", "jpg", "jpeg").contains(name.substringAfterLast(".").lowercase())
                    }
                    it.isVisible = true
                }

                dialog.file?.also { onImageSelect.invoke(Path.of(it)) }
            }
        }
        Spacer(Modifier.height(8.dp))
        image?.apply {
            Image(
                bitmap = Image.convert(this).awt().toComposeImageBitmap(),
                "",
            )
        }
        Spacer(Modifier.height(8.dp))
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
    manager: DeviceManager,
    onClick: () -> Unit
) {
    Button(onClick) {
        Text("Upload")
    }
}