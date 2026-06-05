package app.lawnchair.bb10hub

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory notification repository with StateFlow for reactive updates
 */
class HubRepository {
    private val _notifications = MutableStateFlow<List<HubNotification>>(emptyList())
    val notifications: StateFlow<List<HubNotification>> = _notifications.asStateFlow()
    
    private val _settings = MutableStateFlow(HubSettings())
    val settings: StateFlow<HubSettings> = _settings.asStateFlow()
    
    fun addNotification(notification: HubNotification) {
        _notifications.update { current ->
            (listOf(notification) + current)
                .distinctBy { it.key }
                .sortedByDescending { it.timestamp }
        }
    }
    
    fun removeNotification(key: String) {
        _notifications.update { current ->
            current.filter { it.key != key }
        }
    }
    
    fun updateNotification(key: String, update: (HubNotification) -> HubNotification) {
        _notifications.update { current ->
            current.map { if (it.key == key) update(it) else it }
        }
    }
    
    fun snoozeNotification(key: String, durationMs: Long) {
        updateNotification(key) {
            it.copy(
                isSnoozed = true,
                snoozeUntil = System.currentTimeMillis() + durationMs
            )
        }
    }
    
    fun markAsRead(key: String) {
        updateNotification(key) { it.copy(isRead = true) }
    }
    
    fun clearAll() {
        _notifications.value = emptyList()
    }
    
    fun updateSettings(settings: HubSettings) {
        _settings.value = settings
    }
    
    fun getGroupedNotifications(): List<NotificationGroup> {
        val current = _notifications.value
        val activeNotifications = current.filter { !it.isSnoozed }
        
        return NotificationCategory.values()
            .map { category ->
                NotificationGroup(
                    category = category,
                    notifications = activeNotifications.filter { it.category == category }
                )
            }
            .filter { it.notifications.isNotEmpty() }
    }
}

// Singleton instance
object HubRepositoryManager {
    private var instance: HubRepository? = null
    
    fun getInstance(): HubRepository {
        return instance ?: HubRepository().also { instance = it }
    }
}
