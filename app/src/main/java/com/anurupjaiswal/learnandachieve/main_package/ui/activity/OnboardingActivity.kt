package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.databinding.ActivityOnbordingBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.OnboardingAdapter
import com.anurupjaiswal.learnandachieve.model.OnboardingItem

class OnboardingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOnbordingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var dots: Array<ImageView>
    private lateinit var onboardingItems: List<OnboardingItem>
    private lateinit var sharedPreferences: SharedPreferences
    private var isOnboardingCompleted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        if (isOnboardingCompleted) {
            // Redirect to the main activity or login
            redirectToLoginActivity()
            return
        }


        // Set up view binding

        binding = ActivityOnbordingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize onboarding items
        onboardingItems = getOnboardingItems()

        // Set up the adapter
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = onboardingAdapter

        // Set up the custom dots
        setupDots(onboardingItems.size)
        updateBackgroundAndStatusBarColor(0)

        // Set up page change callback
        binding.viewPager.registerOnPageChangeCallback(viewPagerCallback)

        // Set the next button click listener
        binding.mcvNext.setOnClickListener(this)
        binding.tvSkip.setOnClickListener(this)
    }

    private val viewPagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            updateDots(position)
            updateBackgroundAndStatusBarColor(position)


            if (position == onboardingItems.size - 1) {
                // Last page: Show "Start" button and animate the card to full width
                binding.tvStart.visibility = View.VISIBLE
                animateCardToFullWidth(binding.mcvNext)
                binding.ivNext.visibility = View.GONE
                binding.tvSkip.visibility = View.INVISIBLE

            } else {
                // Not the last page: Hide "Start" button and reset card width
                binding.tvStart.visibility = View.GONE
                binding.ivNext.visibility = View.VISIBLE
                binding.tvSkip.visibility = View.VISIBLE

                resetCardWidth(binding.mcvNext)
            }
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)

            // Detect halfway point to change background color smoothly
            if (positionOffset > 0.5f && position + 1 < onboardingItems.size) {
                updateBackgroundAndStatusBarColor(position + 1)

            } else {
                updateBackgroundAndStatusBarColor(position)
            }
        }
    }

    private fun getOnboardingItems(): List<OnboardingItem> {
        return listOf(
            OnboardingItem(
                R.drawable.onboarding_img1,
                "Discover Your Learning Path",
                "Choose from a variety of topics to start your personalized learning journey.",
                ContextCompat.getColor(this, R.color.brightOrange),
                ContextCompat.getColor(this, R.color.burntOrange),
                ContextCompat.getColor(this, R.color.inactiveOrange)
            ),
            OnboardingItem(
                R.drawable.onboarding_img2,
                "Interactive Lessons",
                "Engage with interactive lessons designed to make learning fun and effective.",
                ContextCompat.getColor(this, R.color.blue),
                ContextCompat.getColor(this, R.color.navyBlue),
                ContextCompat.getColor(this, R.color.inactiveBlueColor)
            ),
            OnboardingItem(
                R.drawable.onboarding_img3,
                "Track Your Progress",
                "Stay motivated by tracking your progress. Earn badges and rewards as you complete lessons.",
                ContextCompat.getColor(this, R.color.tealGreen),
                ContextCompat.getColor(this, R.color.green31),
                ContextCompat.getColor(this, R.color.inactiveGreen)
            )
        )
    }

    private fun setupDots(count: Int) {
        dots = Array(count) { ImageView(this) }
        for (i in dots.indices) {
            dots[i] = ImageView(this).apply {
                setImageDrawable(ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.inactive_dot))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                    marginStart = 8
                }
            }
            binding.dotsLayout.addView(dots[i])
        }
        updateDots(0) // Set the first dot as active initially
    }

    private fun updateDots(position: Int) {
        dots.forEachIndexed { index, dot ->
            if (index == position) {
                dot.setImageResource(R.drawable.active_dot)
                dot.setColorFilter(onboardingItems[position].activeDotColor, android.graphics.PorterDuff.Mode.SRC_IN)
                fadeInAnimation(dot)
            } else {
                dot.setImageResource(R.drawable.inactive_dot)
                dot.setColorFilter(onboardingItems[position].inactiveDotColor, android.graphics.PorterDuff.Mode.SRC_IN)
                fadeOutAnimation(dot)
            }
        }
    }

    private fun updateBackgroundAndStatusBarColor(position: Int) {
        val color = onboardingItems[position].backgroundColor
        binding.root.setBackgroundColor(color)
        window.statusBarColor = color
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.mcvNext -> {
                val currentPosition = binding.viewPager.currentItem
                if (currentPosition < onboardingItems.size - 1) {
                    binding.viewPager.currentItem = currentPosition + 1
                } else {
                    completeOnboarding()
                }
            }
            R.id.tvSkip ->{
                completeOnboarding()
            }
        }
    }



    private fun fadeInAnimation(dot: ImageView) {
        ObjectAnimator.ofFloat(dot, "alpha", 0f, 1f).apply {
            duration = 300L
            start()
        }
    }

    private fun fadeOutAnimation(dot: ImageView) {
        ObjectAnimator.ofFloat(dot, "alpha", 1f, 0.5f).apply {
            duration = 300L
            start()
        }
    }

    private fun animateCardToFullWidth(card: MaterialCardView) {
        // Get the full width of the screen
        val targetWidth = resources.displayMetrics.widthPixels

        // Set up an animator
        val animator = ValueAnimator.ofInt(card.width, targetWidth).apply {
            duration = 300
            addUpdateListener { animation ->
                val newWidth = animation.animatedValue as Int
                card.layoutParams.width = newWidth
                card.requestLayout() // Request layout to apply changes
            }
        }
        animator.start()
    }



    private fun completeOnboarding() {
        // Set onboarding as completed
        isOnboardingCompleted = true
        sharedPreferences.edit().putBoolean("onboarding_completed", true).apply() // Save to SharedPreferences
        redirectToLoginActivity()
    }

    private fun redirectToLoginActivity() {
        // Start your main activity here
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Finish the onboarding activity
    }
    private fun resetCardWidth(card: MaterialCardView) {
        // Reset the card width to wrap_content
        card.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
        card.requestLayout() // Force the layout to update
    }


}
