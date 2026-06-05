package app.lawnchair.bb10hub

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.graphics.ColorUtils

/**
 * Controls Xperia LED light colors per notification category
 */
class HubLedController(private val context: Context) {
    private val notificationManager = 
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    fun flashForCategory(category: NotificationCategory) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return // LED via NotificationChannel requires Android 8+
        }
        
        val color = getCategoryColor(category)
        
        try {
            // Create or update notification channel with LED color
            val channelId = "bb10_led_${category.name}"
            val channel = NotificationChannelCompat.Builder(channelId, NotificationManager.IMPORTANCE_LOW)
                .setName("BB10 Hub - ${category.displayName}")
                .setLightColor(color)
                .build()
            
            notificationManager.createNotificationChannel(channel)
        } catch (e: Exception) {
            // If LED control isn't available, fail silently
        }
    }
    
    private fun getCategoryColor(category: NotificationCategory): Int {
        return try {
            android.graphics.Color.parseColor(category.colorHex)
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.WHITE
        }
    }
}
