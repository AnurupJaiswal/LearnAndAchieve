package com.anurupjaiswal.learnandachieve.basic.security

import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils

abstract class SecureBaseFragment : Fragment() {

    // Child fragments must supply their root view for security handling
    protected abstract fun getSecureRootView(): View

    override fun onResume() {
        super.onResume()
        // Prevent screenshots and screen recording by setting FLAG_SECURE
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        // Check for overlays and hide content if an overlay is enabled
        if (isOverlayEnabled()) {
            getSecureRootView().visibility = View.INVISIBLE
            // Show your custom toast message using your Utils.T function
            Utils.T(requireContext(), "Overlay detected! Content hidden.")
        } else {
            getSecureRootView().visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        // Clear FLAG_SECURE when this fragment is no longer visible
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    // Helper method to check overlay permission (a proxy for active overlays)
    private fun isOverlayEnabled(): Boolean {
        return Settings.canDrawOverlays(requireContext())
    }
}