package bluetooth

import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.io.StreamConnection

class DeviceResponseHandler(
    private val manager: DeviceConnectionManager,
    connection: StreamConnection
) : Thread() {
    companion object {
        private const val BUF_SIZE = 1024
        private const val ACK_MESSAGE = "Ok!"
    }

    private val input = connection.openInputStream()
    private val running = AtomicBoolean(true)

    fun close() {
        running.set(false)
        input.close()
    }

    override fun run() {
        val buffer = ByteArray(BUF_SIZE)

        while(running.get()) {
            val available = input.available()
            if (available > 0) {
                val amount = input.readNBytes(buffer, 0, available)
                val message = String(buffer, 0, amount)

                if (message == ACK_MESSAGE) {
                    val status = manager.status()

                    if (status is DeviceStatus.WaitingForResponse)
                        manager.setStatus(status.next)
                }
            }
        }
    }
}