package bluetooth

import javax.bluetooth.*
import javax.microedition.io.Connector
import javax.microedition.io.StreamConnection

class DeviceConnectionManager(
    internal val status: () -> DeviceStatus,
    internal val setStatus: (DeviceStatus) -> Unit,
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
    var connection: DeviceCommunicationManager? = null; private set

    fun startScan() {
        agent.startInquiry(DiscoveryAgent.GIAC, this)
        setStatus.invoke(DeviceStatus.Scanning)
    }

    fun stopScan() {
        agent.cancelInquiry(this)
        setStatus.invoke(DeviceStatus.Disconnected)
    }

    fun shutdown() {
        connection?.stop()
        setStatus.invoke(DeviceStatus.Disconnected)
    }

    override fun deviceDiscovered(device: RemoteDevice, type: DeviceClass) {
        if (device.bluetoothAddress == DEVICE_MAC_ADDRESS) {
            agent.cancelInquiry(this)

            setStatus.invoke(DeviceStatus.Connecting)

            val service = agent.selectService(SERVICE_UUID, 0, true)
            connection =
                DeviceCommunicationManager(this, Connector.open(service) as StreamConnection)
                    .also(DeviceCommunicationManager::identify)

            setStatus.invoke(DeviceStatus.WaitingForResponse(DeviceStatus.Connected))
        }
    }

    override fun servicesDiscovered(a: Int, b: Array<out ServiceRecord>) {}

    override fun serviceSearchCompleted(a: Int, b: Int) {}

    override fun inquiryCompleted(p0: Int) {}
}