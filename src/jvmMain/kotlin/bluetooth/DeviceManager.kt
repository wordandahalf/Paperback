package bluetooth

import java.io.DataOutputStream
import java.nio.file.Path
import javax.bluetooth.*
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection

class DeviceManager(
    private val stateHandler: (DeviceStatus) -> Unit,
) : DiscoveryListener {
    companion object {
        const val DEVICE_MAC_ADDRESS = "78E36D0D5ABA"
        val SERVICE_UUID = UUID("0000110100001000800000805F9B34FB", false)

        const val DISPLAY_TYPE: Byte = 25

        const val COMMAND_IDENTIFY_PREFIX   = 'I'.code.toByte()
        const val COMMAND_NEXT_PREFIX       = 'N'.code.toByte()
        const val COMMAND_SHOW_PREFIX       = 'S'.code.toByte()
    }

    /**
     * The Bluetooth agent that provides the discovery of devices
     */
    private val agent = LocalDevice.getLocalDevice().discoveryAgent

    /**
     * The stream to the connected device
     */
    private var connection: DataOutputStream? = null

    fun startScan() {
        agent.startInquiry(DiscoveryAgent.GIAC, this)
        stateHandler.invoke(DeviceStatus.SCANNING)
    }

    fun stopScan() {
        agent.cancelInquiry(this)
        stateHandler.invoke(DeviceStatus.DISCONNECTED)
    }

    fun shutdown() {
        stateHandler.invoke(DeviceStatus.DISCONNECTED)
    }

    override fun deviceDiscovered(device: RemoteDevice, type: DeviceClass) {
        if (device.bluetoothAddress == DEVICE_MAC_ADDRESS) {
            agent.cancelInquiry(this)

            stateHandler.invoke(DeviceStatus.CONNECTING)

            val service = agent.selectService(SERVICE_UUID, 0, true)
            connection = (Connector.open(service) as StreamConnection).openDataOutputStream()

            stateHandler.invoke(DeviceStatus.CONNECTED)

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