package app.lawnchair.bb10hub

import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.material3.darkColorScheme
import com.android.launcher3.Launcher
import com.android.launcher3.LauncherRootView
import com.android.launcher3.views.AbstractPredictionRowView
import com.android.launcher3.views.OverlayCallbackImpl

/**
 * BB10 Hub overlay integration with Lawnchair/Launcher3
 * 
 * This class is installed as the default overlay for left-swipe handling,
 * replacing Google Discover with the BB10 Hub.
 */
class BB10HubOverlay(launcher: Launcher) : OverlayCallbackImpl(launcher) {
    private val tag = "BB10HubOverlay"
    private var scrollProgress = 0f
    
    init {
        Log.d(tag, "BB10HubOverlay initialized")
        ensureInstalled()
    }
    
    private fun ensureInstalled() {
        try {
            launcher.setLauncherOverlay(this)
            Log.d(tag, "Overlay installed successfully")
        } catch (e: Exception) {
            Log.e(tag, "Failed to install overlay: ${e.message}")
        }
    }
    
    /**
     * Called when the user starts swiping from the left edge
     */
    override fun onScrollInteractionBegin(up: Boolean) {
        Log.d(tag, "Scroll interaction begin (up: $up)")
        super.onScrollInteractionBegin(up)
    }
    
    /**
     * Called as the user swipes left (scroll > 0 means swiping into feed)
     * 
     * @param displacement 0f = home, 1f = fully opened feed
     */
    override fun onScrollChange(displacement: Float, up: Boolean) {
        Log.d(tag, "Scroll change: displacement=$displacement")
        scrollProgress = displacement
        
        // If user has swiped past threshold (e.g., 30%), open Hub
        if (displacement > 0.3f && scrollProgress <= 0.31f) {
            openHub()
        }
        
        super.onScrollChange(displacement, up)
    }
    
    /**
     * Called when scroll interaction ends
     */
    override fun onScrollInteractionEnd(up: Boolean) {
        Log.d(tag, "Scroll interaction end (up: $up)")
        super.onScrollInteractionEnd(up)
    }
    
    /**
     * Open the BB10 Hub activity
     */
    private fun openHub() {
        Log.d(tag, "Opening BB10 Hub")
        try {
            val intent = Intent(launcher, Bb10HubActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            launcher.startActivity(intent)
        } catch (e: Exception) {
            Log.e(tag, "Failed to open Hub: ${e.message}")
        }
    }
    
    companion object {
        /**
         * Factory method called by Lawnchair to create the overlay
         */
        @JvmStatic
        fun createOverlay(launcher: Launcher): BB10HubOverlay {
            return BB10HubOverlay(launcher)
        }
    }
}
