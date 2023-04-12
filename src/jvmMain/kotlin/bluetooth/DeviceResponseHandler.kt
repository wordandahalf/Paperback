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
        var offset = 0

        while(running.get()) {
            var available = input.available()

            // Read the stream to completion. This is a terribly
            // inefficient
            while (available > 0) {
                buffer[offset++] = input.read().toByte()
                available = input.available()
            }

            if (offset == 0)
                continue

            val message = String(buffer, 0, offset)

            println("\"$message\"")

            if (message == ACK_MESSAGE) {
                val status = manager.status()

                if (status is DeviceStatus.WaitingForResponse)
                    manager.setStatus(status.next)
            }

            println(manager.status())

            if (manager.status() == DeviceStatus.Displaying)
                manager.connection!!.show()

            offset = 0
        }
    }
}