package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    val activity: Activity = this@SplashScreenActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up ViewBinding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()
        setupVideoView()

        // Check if onboarding is completed
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        // Start a new thread to handle the delay and transition
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(5000) // Wait for 5 seconds
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (!isOnboardingCompleted) {
                        // Redirect to OnboardingActivity if onboarding is not completed
                        Utils.I_clear(activity, OnboardingActivity::class.java, null)
                    } else if (Utils.IS_LOGIN()) {
                        // Redirect to DashboardActivity if logged in
                        Utils.I_clear(activity, DashboardActivity::class.java, null)
                    } else {
                        // Redirect to LoginActivity if not logged in
                        Utils.I_clear(activity, LoginActivity::class.java, null)
                    }
                }
            }
        }
        thread.start()
    }

    private fun setupVideoView() {
        val videoView = binding.videoView

        // Create the URI to access the video from the raw folder
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)

        // Set the video URI
        videoView.setVideoURI(videoUri)

        // Retrieve screen dimensions at runtime
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = resources.displayMetrics
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // Set the layout parameters to dynamically match screen dimensions
        val layoutParams = videoView.layoutParams
        layoutParams.width = screenWidth // Set the width to the screen width
        layoutParams.height = screenHeight // Set the height to the screen height
        videoView.layoutParams = layoutParams

        // Start the video when it is ready
        videoView.setOnPreparedListener { mediaPlayer ->

            videoView.start()
        }

        // Optional: Handle scaling for specific aspect ratios if needed
        videoView.setOnErrorListener { _, what, extra ->
            Utils.E("Video playback error: what=$what, extra=$extra")
            true
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.main).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
