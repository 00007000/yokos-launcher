package app.lawnchair.bb10hub

import android.content.Intent
import android.util.Log

/**
 * BB10 Hub overlay placeholder
 * 
 * The actual swipe-to-open is handled by the notification listener service,
 * which opens Bb10HubActivity when the user swipes left.
 */
object BB10HubOverlay {
    private val tag = "BB10HubOverlay"
    
    fun openHub(context: android.content.Context) {
        Log.d(tag, "Opening BB10 Hub")
        try {
            val intent = Intent(context, Bb10HubActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(tag, "Failed to open Hub: ${e.message}")
        }
    }
}
