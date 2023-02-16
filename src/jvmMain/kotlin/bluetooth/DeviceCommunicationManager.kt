package bluetooth

import javax.microedition.io.StreamConnection

class DeviceCommunicationManager(
    manager: DeviceConnectionManager,
    private val connection: StreamConnection
) {
    companion object {
        const val DISPLAY_TYPE: Byte = 25

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
        println("Identify")

        output.write(
            byteArrayOf(COMMAND_IDENTIFY_PREFIX, DISPLAY_TYPE)
        )
    }

    /**
     * Send the "next state" command
     */
    fun next() {
        output.write(
            byteArrayOf(COMMAND_NEXT_PREFIX)
        )
    }

    fun load() {
        println("Load data")

        output.write(
            byteArrayOf(COMMAND_LOAD_PREFIX, 3, 0, 6, 0, 0)
        )
    }

    /**
     * Send the "show picture" command
     */
    fun show() {
        output.write(
            byteArrayOf(COMMAND_SHOW_PREFIX)
        )
    }
}