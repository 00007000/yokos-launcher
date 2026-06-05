package app.lawnchair.bb10hub

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * System notification listener that feeds all notifications into the Hub
 * 
 * To grant permission on Android 13+:
 * adb shell cmd notification allow_listener app.lawnchair.debug/app.lawnchair.bb10hub.HubNotificationService
 */
class HubNotificationService : NotificationListenerService() {
    private val tag = "HubNotificationService"
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var repository: HubRepository
    private lateinit var ledController: HubLedController
    
    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "HubNotificationService created")
        
        repository = HubRepositoryManager.getInstance()
        ledController = HubLedController(this)
        
        // Apply saved settings when service starts
        applySettings()
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d(tag, "Notification posted: ${sbn.packageName} - ${sbn.notification.contentTitle}")
        
        val notification = sbn.notification
        
        // Skip certain notifications (notifications with FLAG_NO_CLEAR, ongoing, etc.)
        if (notification.flags and (Notification.FLAG_NO_CLEAR or Notification.FLAG_ONGOING_EVENT) != 0) {
            return
        }
        
        scope.launch {
            val hubNotification = createHubNotification(sbn)
            repository.addNotification(hubNotification)
            
            // Flash LED if enabled
            if (repository.settings.value.ledEnabled) {
                ledController.flashForCategory(hubNotification.category)
            }
            
            // Show peek animation (handled by activity if it's open)
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(tag, "Notification removed: ${sbn.packageName}")
        scope.launch {
            repository.removeNotification(sbn.key)
        }
    }
    
    private fun createHubNotification(sbn: StatusBarNotification): HubNotification {
        val notification = sbn.notification
        val extras = notification.extras
        
        val title = extras.getString(Notification.EXTRA_TITLE) ?: sbn.packageName
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        
        return HubNotification(
            key = sbn.key,
            packageName = sbn.packageName,
            title = title,
            text = text,
            timestamp = sbn.postTime,
            category = NotificationCategory.fromTag(
                notification.category,
                sbn.packageName
            ),
            contentIntent = notification.contentIntent,
            deleteIntent = notification.deleteIntent
        )
    }
    
    private fun applySettings() {
        // Load settings from SharedPreferences if available
        val prefs = getSharedPreferences("bb10_hub_prefs", Context.MODE_PRIVATE)
        val settings = HubSettings(
            peekEnabled = prefs.getBoolean("peek_enabled", true),
            ledEnabled = prefs.getBoolean("led_enabled", true),
            groupByCategory = prefs.getBoolean("group_by_category", true),
            diagnosticsEnabled = prefs.getBoolean("diagnostics_enabled", false)
        )
        repository.updateSettings(settings)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "HubNotificationService destroyed")
    }
}
