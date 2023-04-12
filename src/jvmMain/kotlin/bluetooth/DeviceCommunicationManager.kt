package bluetooth

import com.sksamuel.scrimage.ImmutableImage
import image.Image
import image.palette.BuiltinPalette
import image.toPackedArray
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import javax.microedition.io.StreamConnection

class DeviceCommunicationManager(
    private val manager: DeviceConnectionManager,
    private val connection: StreamConnection
) {
    companion object {
        const val DISPLAY_TYPE: Byte = 25

        const val BATCH_SIZE = 2050

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
        manager.setStatus(DeviceStatus.Uploading)
        println("Load data")

//        val data = image.toPackedArray(BuiltinPalette)

        val data = ByteArray(Image.WIDTH * Image.HEIGHT / 2)
        val length = data.size

        val buf = ByteBuffer.allocate(6)

        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.put(COMMAND_LOAD_PREFIX)
        buf.putShort(length.toShort())
        buf.put(byteArrayOf(length.and(0xFF).toByte(), length.shr(8).and(0xFF).toByte(), length.shr(16).and(0xFF).toByte()))

        output.write(buf.array())
        output.write(data)
        output.flush()

        manager.setStatus(DeviceStatus.WaitingForResponse(DeviceStatus.Displaying))
    }

    /**
     * Send the "show picture" command
     */
    fun show() {
        output.write(
            byteArrayOf(COMMAND_SHOW_PREFIX)
        )

        manager.setStatus(DeviceStatus.WaitingForResponse(DeviceStatus.Connected))
    }
}