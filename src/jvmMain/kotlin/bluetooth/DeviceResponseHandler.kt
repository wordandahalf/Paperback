package bluetooth

import java.util.concurrent.atomic.AtomicBoolean
import javax.microedition.io.StreamConnection

class DeviceResponseHandler(
    private val manager: DeviceConnectionManager,
    connection: StreamConnection
) : Thread() {
    companion object {
        private const val BUF_SIZE = 1024

        private const val ACK_MESSAGE = "Ok"
        private const val FAIL_MESSAGE = "Error"

        private const val DELIMITER = '!'.code
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

            // Read the stream to completion. This is not the best way,
            // but it's a way that I know works.
            while (available > 0) {
                val next = input.read()

                if (next == DELIMITER)
                    break

                buffer[offset++] = next.toByte()
                available = input.available()
            }

            if (offset == 0)
                continue

            when (val message = String(buffer, 0, offset)) {
                ACK_MESSAGE -> handleAcknowledge()
                FAIL_MESSAGE -> handleError()
                else ->
                    println("Unknown response '$message'")
            }

            offset = 0
        }
    }

    private fun handleAcknowledge() {
        when (val status = manager.status()) {
            is DeviceStatus.WaitingForResponse ->
                manager.setStatus(status.next)
            is DeviceStatus.Uploading ->
                if (status.index < status.data.size) {
                    println("Sending data... ${status.data.size - status.index} bytes remaining...")
                    manager.connection!!.sendData()
                }
                else {
                    println("Done sending data... sending show command.")
                    manager.connection!!.show()
                }
            else -> {}
        }

        if (manager.status() == DeviceStatus.Displaying)
            manager.connection!!.show()
    }
    private fun handleError() {
        manager.setStatus(DeviceStatus.Error)
    }
}