package app.lawnchair.bb10hub

import android.app.PendingIntent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import android.util.Log

class Bb10HubActivity : ComponentActivity() {
    private lateinit var repository: HubRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        repository = HubRepositoryManager.getInstance()
        
        setContent {
            val darkColorScheme = darkColorScheme(
                primary = Color(0xFF0084FF),
                secondary = Color(0xFF31A24C),
                tertiary = Color(0xFFE4405F),
                background = Color(0xFF1A1A1A),
                surface = Color(0xFF2A2A2A),
                onBackground = Color.White,
                onSurface = Color.White
            )
            
            MaterialTheme(colorScheme = darkColorScheme) {
                BB10HubScreen(
                    repository = repository,
                    onNotificationDismiss = { key ->
                        Log.d("BB10HubActivity", "Dismissing notification: $key")
                        val notif = repository.notifications.value.find { it.key == key }
                        notif?.deleteIntent?.send()
                        repository.removeNotification(key)
                    },
                    onNotificationTap = { key ->
                        Log.d("BB10HubActivity", "Tapping notification: $key")
                        val notif = repository.notifications.value.find { it.key == key }
                        try {
                            notif?.contentIntent?.send()
                        } catch (e: PendingIntent.CanceledException) {
                            Log.w("BB10HubActivity", "Content intent canceled: ${e.message}")
                        }
                        finish()
                    },
                    onSnooze = { key, durationMs ->
                        Log.d("BB10HubActivity", "Snoozing notification: $key for $durationMs ms")
                        repository.snoozeNotification(key, durationMs)
                    }
                )
            }
        }
    }
}
