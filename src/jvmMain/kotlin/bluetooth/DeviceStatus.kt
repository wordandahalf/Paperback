package bluetooth

/**
 *
 */
sealed class DeviceStatus {
    object Disconnected : DeviceStatus()
    object Scanning : DeviceStatus()
    object Connecting : DeviceStatus()
    object Connected : DeviceStatus()
    data class WaitingForResponse(val next: DeviceStatus) : DeviceStatus()
    object Uploading : DeviceStatus()
}