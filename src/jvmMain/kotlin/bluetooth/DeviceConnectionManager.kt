package bluetooth

import javax.bluetooth.*
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection

class DeviceConnectionManager(
    private val stateHandler: (DeviceStatus) -> Unit,
) : DiscoveryListener {
    companion object {
        const val DEVICE_MAC_ADDRESS = "78E36D0D5ABA"
        val SERVICE_UUID = UUID("0000110100001000800000805F9B34FB", false)
    }

    /**
     * The Bluetooth agent that provides the discovery of devices
     */
    private val agent = LocalDevice.getLocalDevice().discoveryAgent

    /**
     * The manager of the connection to the connected device
     */
    private var connection: DeviceCommunicationManager? = null

    fun startScan() {
        agent.startInquiry(DiscoveryAgent.GIAC, this)
        stateHandler.invoke(DeviceStatus.SCANNING)
    }

    fun stopScan() {
        agent.cancelInquiry(this)
        stateHandler.invoke(DeviceStatus.DISCONNECTED)
    }

    fun shutdown() {
        connection?.stop()
        stateHandler.invoke(DeviceStatus.DISCONNECTED)
    }

    override fun deviceDiscovered(device: RemoteDevice, type: DeviceClass) {
        if (device.bluetoothAddress == DEVICE_MAC_ADDRESS) {
            agent.cancelInquiry(this)

            stateHandler.invoke(DeviceStatus.CONNECTING)

            val service = agent.selectService(SERVICE_UUID, 0, true)
            connection =
                DeviceCommunicationManager(Connector.open(service) as StreamConnection)
                    .also(DeviceCommunicationManager::identify)

            stateHandler.invoke(DeviceStatus.CONNECTED)
        }
    }

    override fun servicesDiscovered(a: Int, b: Array<out ServiceRecord>) {}

    override fun serviceSearchCompleted(a: Int, b: Int) {}

    override fun inquiryCompleted(p0: Int) {}
}