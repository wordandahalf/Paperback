package bluetooth

import java.io.DataOutputStream
import javax.bluetooth.*
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection

class BluetoothManager(
    private val updateState: (ConnectionStatus) -> Unit
) : DiscoveryListener {
    companion object {
        const val DEVICE_MAC_ADDRESS = "78E36D0D5ABA"
        val SERVICE_UUID = UUID("0000110100001000800000805F9B34FB", false)

        const val DISPLAY_TYPE: Byte = 25

        const val COMMAND_IDENTIFY_PREFIX   = 'I'.code.toByte()
        const val COMMAND_NEXT_PREFIX       = 'N'.code.toByte()
        const val COMMAND_SHOW_PREFIX       = 'S'.code.toByte()
    }

    private val agent = LocalDevice.getLocalDevice().discoveryAgent
    private var connection: DataOutputStream? = null

    fun startScan() {
        println("Starting scan...")
        agent.startInquiry(DiscoveryAgent.GIAC, this)
        updateState.invoke(ConnectionStatus.SCANNING)
    }

    fun stopScan() {
        agent.cancelInquiry(this)
        updateState.invoke(ConnectionStatus.DISCONNECTED)
    }

    fun shutdown() {
        updateState.invoke(ConnectionStatus.DISCONNECTED)
    }

    override fun deviceDiscovered(device: RemoteDevice, type: DeviceClass) {
        if (device.bluetoothAddress == DEVICE_MAC_ADDRESS) {
            agent.cancelInquiry(this)

            println("Found device: ${device.bluetoothAddress}")
            updateState.invoke(ConnectionStatus.CONNECTING)

            val service = agent.selectService(SERVICE_UUID, 0, true)
            connection = (Connector.open(service) as StreamConnection).openDataOutputStream()

            updateState.invoke(ConnectionStatus.CONNECTED)

            identify()
        }
    }

    override fun servicesDiscovered(a: Int, b: Array<out ServiceRecord>) {}

    override fun serviceSearchCompleted(a: Int, b: Int) {}

    override fun inquiryCompleted(p0: Int) {}

    /**
     * Send the "identify" command
     */
    private fun identify() {
        (connection ?: throw IllegalStateException())
            .write(
                byteArrayOf(COMMAND_IDENTIFY_PREFIX, DISPLAY_TYPE)
            )
    }

    /**
     * Send the "next state" command
     */
    private fun next() {
        (connection ?: throw IllegalStateException())
            .write(
                byteArrayOf(COMMAND_NEXT_PREFIX)
            )
    }

    /**
     * Send the "show picture" command
     */
    private fun show() {
        (connection ?: throw IllegalStateException())
            .write(
                byteArrayOf(COMMAND_SHOW_PREFIX)
            )
    }
}