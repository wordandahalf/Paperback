import com.welie.blessed.BluetoothCentralManager
import com.welie.blessed.BluetoothCentralManagerCallback
import com.welie.blessed.BluetoothPeripheral
import com.welie.blessed.ScanResult

class BluetoothManager(
    val updateState: (ConnectionStatus) -> Unit
) {
    companion object {
        const val DEVICE_MAC_ADDRESS = "78:E3:6D:0D:5A:BA"
    }

    private val manager =
        BluetoothCentralManager(Handler()).also {
//            it.setRssiThreshold(10)
        }

    private inner class Handler : BluetoothCentralManagerCallback() {
        override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
            if (peripheral.address == DEVICE_MAC_ADDRESS) {
                manager.stopScan()
                peripheral.connect()
            }
        }

        override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
            updateState.invoke(ConnectionStatus.CONNECTED)
        }
    }

    fun startScan() {
        manager.scanForPeripheralsWithAddresses(arrayOf(DEVICE_MAC_ADDRESS))
        updateState.invoke(ConnectionStatus.SCANNING)
    }

    fun stopScan() {
        manager.stopScan()
        updateState.invoke(ConnectionStatus.DISCONNECTED)
    }

    fun shutdown() {
        manager.adapterOff()
        updateState.invoke(ConnectionStatus.DISCONNECTED)
    }
}