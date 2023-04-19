import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import bluetooth.DeviceConnectionManager
import bluetooth.DeviceStatus
import image.Image
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path

@Composable
@Preview
fun ApplicationScope.PaperbackApplication() {
    var status by remember { mutableStateOf<DeviceStatus>(DeviceStatus.Disconnected) }
    var imagePath by remember { mutableStateOf<Path?>(null) }

    val manager = DeviceConnectionManager({ status }, { status = it })

    Window(
        state = WindowState(size = DpSize(320.dp, 640.dp)),
        title = "Paperback", onCloseRequest = { manager.shutdown(); exitApplication() }, resizable = false
    ) {
        MaterialTheme {
            when (status) {
                DeviceStatus.Connected ->
                    UploadView(window, manager, imagePath) { imagePath = it }
                else -> ConnectView(manager, status)
            }
        }
    }
}

@Composable
fun ConnectView(
    manager: DeviceConnectionManager,
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
    manager: DeviceConnectionManager,
    status: DeviceStatus
) {
    when (status) {
        DeviceStatus.Disconnected ->
            manager.startScan()
        DeviceStatus.Scanning ->
            manager.stopScan()
        DeviceStatus.Connecting ->
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
            DeviceStatus.Error ->
                Text("Error. Please restart application.")
            DeviceStatus.Disconnected ->
                Text("Connect")
            DeviceStatus.Scanning ->
                Text("Scanning...")
            DeviceStatus.Connecting ->
                Text("Connecting...")
            DeviceStatus.Displaying ->
                Text("Displaying...")
            is DeviceStatus.Uploading ->
                Text("Uploading...")
            is DeviceStatus.WaitingForResponse ->
                Text("Waiting for response...")
            else ->
                throw IllegalStateException("illegal state ${status::class.java.simpleName}")
        }
    }
}

@Composable
fun UploadView(
    window: ComposeWindow,
    manager: DeviceConnectionManager,
    imagePath:   Path?,
    onImageSelect: (Path) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.25f),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("Please select an image to upload")
        }

        val image = imagePath?.let {
            try {
                Image.convert(it)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        Row {
            if (imagePath == null)
                Text("No image chosen.")
            else
                image?.awt()?.toComposeImageBitmap()?.let { Image(it, "") }
        }
        Spacer(Modifier.height(8.dp))
        Row {
            ChooseFileButton {
                val dialog = FileDialog(window).also {
//                    it.setFilenameFilter { _, name ->
//                        listOf("png", "jpg", "jpeg").contains(name.substringAfterLast(".").lowercase())
//                    }
                    it.isVisible = true
                }

                dialog.file?.also { onImageSelect.invoke(File(dialog.directory, it).toPath()) }
            }
            image?.also {
                Spacer(Modifier.width(8.dp))
                UploadButton {
                    manager.connection?.load(it)
                }
            }
        }
    }
}

@Composable
fun ChooseFileButton(
    onClick: () -> Unit
) {
    Button(onClick) {
        Text("Choose...")
    }
}

@Composable
fun UploadButton(
    onClick: () -> Unit
) {
    Button(onClick) {
        Text("Upload")
    }
}