package com.anurupjaiswal.learnandachieve.basic.security

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils

abstract class SecureBaseFragment : Fragment() {

    companion object {
        private const val TAG = "SecureBaseFragment"
    }

    // Child fragments must supply their secure root view for additional handling.
    protected abstract fun getSecureRootView(): View

    override fun onResume() {
        super.onResume()

        // Prevent screenshots and screen recordings.
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        Log.d(TAG, "FLAG_SECURE set on window.")

        // OPTIONAL: Check for overlay permission for debugging/logging purposes.
        // Note: This only reflects that your app has overlay permission, not whether an overlay is active.
        val overlayStatus = isOverlayEnabled()
        Log.d(TAG, "Overlay permission status: $overlayStatus")

        // Decide if you want to hide the content when the overlay permission is enabled.
        // In many cases, this may not be desirable since it hides your content even if no malicious overlay exists.
        // Uncomment the lines below if you still want to hide content when overlay permission is detected.
        /*
        if (overlayStatus) {
            getSecureRootView().visibility = View.INVISIBLE
            Utils.T(requireContext(), "Overlay detected! Content hidden.")
        } else {
            getSecureRootView().visibility = View.VISIBLE
        }
        */
    }

    override fun onPause() {
        super.onPause()
        // Clear FLAG_SECURE when the fragment is no longer visible.
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        Log.d(TAG, "FLAG_SECURE cleared on window.")
    }

    // Helper method to check overlay permission for API 23+ devices.
    private fun isOverlayEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(requireContext())
        } else {
            false
        }
    }
}
