package app.lawnchair.bb10hub

import android.app.Notification
import android.app.PendingIntent
import android.os.Parcelable
import android.service.notification.StatusBarNotification
import kotlinx.parcelize.Parcelize

/**
 * Notification category enum for sorting and filtering
 */
enum class NotificationCategory(val displayName: String, val colorHex: String) {
    EMAIL("Email", "#0084FF"),
    SMS("Messages", "#31A24C"),
    SOCIAL("Social", "#E4405F"),
    CALLS("Calls", "#FF6B6B"),
    OTHER("Other", "#757575");
    
    companion object {
        fun fromTag(tag: String?, pkg: String?): NotificationCategory {
            val lowerTag = tag?.lowercase() ?: ""
            val lowerPkg = pkg?.lowercase() ?: ""
            
            return when {
                lowerPkg.contains("mail") || lowerPkg.contains("gmail") -> EMAIL
                lowerPkg.contains("sms") || lowerPkg.contains("message") -> SMS
                lowerPkg.contains("whatsapp") || lowerPkg.contains("telegram") -> SMS
                lowerPkg.contains("facebook") || lowerPkg.contains("twitter") || 
                lowerPkg.contains("instagram") || lowerPkg.contains("reddit") -> SOCIAL
                lowerPkg.contains("phone") || lowerPkg.contains("call") -> CALLS
                else -> OTHER
            }
        }
    }
}

/**
 * Hub notification model
 */
@Parcelize
data class HubNotification(
    val key: String,
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long,
    val category: NotificationCategory,
    val contentIntent: PendingIntent? = null,
    val deleteIntent: PendingIntent? = null,
    val isRead: Boolean = false,
    val isSnoozed: Boolean = false,
    val snoozeUntil: Long = 0
) : Parcelable {
    val isExpired: Boolean
        get() = isSnoozed && System.currentTimeMillis() < snoozeUntil
}

/**
 * Hub settings model
 */
data class HubSettings(
    val peekEnabled: Boolean = true,
    val peekDurationMs: Int = 5000,
    val ledEnabled: Boolean = true,
    val groupByCategory: Boolean = true,
    val hiddenCategories: Set<NotificationCategory> = emptySet(),
    val diagnosticsEnabled: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00"
)

/**
 * Grouped notifications for display
 */
data class NotificationGroup(
    val category: NotificationCategory,
    val notifications: List<HubNotification>
)
