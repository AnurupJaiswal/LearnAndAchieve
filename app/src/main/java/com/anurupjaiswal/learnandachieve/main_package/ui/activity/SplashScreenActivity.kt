package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
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

    private lateinit var binding: ActivitySplashScreenBinding  // ViewBinding instance

    val activity: Activity = this@SplashScreenActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up ViewBinding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUI()

        setupVideoView()






        // Start a new thread to handle login and screen transition after the video finishes
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(5000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    // Check if the user is logged in and navigate accordingly
                    Utils.E("Utils.IS_LOGIN()::" + Utils.IS_LOGIN())
                    if (Utils.IS_LOGIN()) {
                        Utils.I_clear(activity, DashboardActivity::class.java, null)
                    } else {
                        Utils.I_clear(activity, LoginActivity::class.java, null)
                    }
                }
            }
        }
        thread.start()
    }




/*
    private fun setupVideoView() {
        val videoView = binding.videoView

        // Create the URI to access the video from the raw folder
        val videoUri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video)

        // Set the video URI
        videoView.setVideoURI(videoUri)

        // Set the layout parameters to make the video fill the screen vertically
        val layoutParams = videoView.layoutParams
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT // Make the video view match the height of the screen
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT  // Make the video view match the width of the screen
        videoView.layoutParams = layoutParams

        // Start the video when it is ready
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // Optionally, loop the video
            videoView.start()
        }
    }
*/



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
            mediaPlayer.isLooping = true // Optionally, loop the video
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
