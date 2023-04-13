package bluetooth

import com.sksamuel.scrimage.ImmutableImage
import image.Image
import image.palette.BuiltinPalette
import image.toPackedArray
import java.lang.Integer.min
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.io.StreamConnection

class DeviceCommunicationManager(
    private val manager: DeviceConnectionManager,
    private val connection: StreamConnection
) {
    companion object {
        const val DISPLAY_TYPE: Byte = 25

        const val MAX_DATA_LENGTH = 250
        const val LOAD_HEADER_LENGTH = 6
        const val MAX_PACKET_LENGTH = LOAD_HEADER_LENGTH + MAX_DATA_LENGTH

        const val COMMAND_IDENTIFY_PREFIX   = 'I'.code.toByte()
        const val COMMAND_NEXT_PREFIX       = 'N'.code.toByte()
        const val COMMAND_LOAD_PREFIX       = 'L'.code.toByte()
        const val COMMAND_SHOW_PREFIX       = 'S'.code.toByte()
    }

    private val responseHandler = DeviceResponseHandler(manager, connection)
        .also(DeviceResponseHandler::start)

    private val output = connection.openDataOutputStream()

    fun stop() {
        responseHandler.close()
        output.close()

        connection.close()
    }

    /**
     * Send the "identify" command
     */
    fun identify() {
        output.write(
            byteArrayOf(COMMAND_IDENTIFY_PREFIX, DISPLAY_TYPE)
        )
    }

    fun load(image: ImmutableImage) {
        manager.setStatus(DeviceStatus.Uploading(image.toPackedArray(BuiltinPalette), 0))
        println("Load data")

        sendData()
    }

    /**
     * Send the "show picture" command
     */
    fun show() {
        manager.setStatus(DeviceStatus.Displaying)
        output.write(
            byteArrayOf(COMMAND_SHOW_PREFIX)
        )

        manager.setStatus(DeviceStatus.WaitingForResponse(DeviceStatus.Connected))
    }

    internal fun sendData() {
        val status = manager.status() as? DeviceStatus.Uploading ?: return

        val dataOffset = status.index
        val dataLength = min(status.data.size - dataOffset, MAX_DATA_LENGTH)

        val packetLength = dataLength + LOAD_HEADER_LENGTH

        val buf = ByteBuffer.allocate(packetLength)
            .also { it.order(ByteOrder.LITTLE_ENDIAN) }

        buf.put(COMMAND_LOAD_PREFIX)
        buf.putShort(packetLength.toShort())

        // The length of the data is the current packet size plus the sum of all
        // previously sent packets' lengths. This quantity can be derived by dividing
        // the offset by the length of the maximum data size.
        val dataSize = packetLength + MAX_PACKET_LENGTH * (dataOffset / MAX_DATA_LENGTH)
        buf.put(byteArrayOf(dataSize.and(0xFF).toByte(), dataSize.shr(8).and(0xFF).toByte(), dataSize.shr(16).and(0xFF).toByte()))
        buf.put(status.data, dataOffset, dataLength)

        output.write(buf.array())
        output.flush()

        status.index += dataLength
    }
}