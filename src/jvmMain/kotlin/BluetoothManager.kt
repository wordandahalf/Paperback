import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothCentralManagerCallback
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ScanResult

object Bluetooth {
    private val manager =
        BluetoothCentralManager(Handler).also {
            it.setRssiThreshold(20)
        }

    var Status = ConnectionStatus.DISCONNECTED; private set

    private object Handler : BluetoothCentralManagerCallback() {
        override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
            if (peripheral.address == "78:E3:6D:0D:5A:BA") {
                manager.stopScan()
                peripheral.connect()
            }
        }

        override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
            Status = ConnectionStatus.CONNECTED
        }
    }

    fun scan() {
        manager.scanForPeripherals()
        Status = ConnectionStatus.SCANNING
    }

    fun stop() {
        manager.stopScan()
        Status = ConnectionStatus.DISCONNECTED
    }

    fun shutdown() {
        manager.
    }
}