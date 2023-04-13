package bluetooth

/**
 *
 */
sealed class DeviceStatus {
    object Error : DeviceStatus()
    object Disconnected : DeviceStatus()
    object Scanning : DeviceStatus()
    object Connecting : DeviceStatus()
    object Connected : DeviceStatus()
    data class WaitingForResponse(val next: DeviceStatus) : DeviceStatus()
    data class Uploading(val data: ByteArray, var index: Int) : DeviceStatus() {
        override fun equals(other: Any?) =
            throw UnsupportedOperationException()

        override fun hashCode() =
            throw UnsupportedOperationException()
    }

    object Displaying : DeviceStatus()
}