package bluetooth

import javax.microedition.io.StreamConnection

class DeviceCommunicationManager(
    private val connection: StreamConnection
) {
    companion object {
        const val DISPLAY_TYPE: Byte = 25

        const val COMMAND_IDENTIFY_PREFIX   = 'I'.code.toByte()
        const val COMMAND_NEXT_PREFIX       = 'N'.code.toByte()
        const val COMMAND_SHOW_PREFIX       = 'S'.code.toByte()
    }

    private val input = connection.openDataInputStream()
    private val output = connection.openDataOutputStream()

    fun stop() {
        input.close()
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

    /**
     * Send the "next state" command
     */
    fun next() {
        output.write(
            byteArrayOf(COMMAND_NEXT_PREFIX)
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