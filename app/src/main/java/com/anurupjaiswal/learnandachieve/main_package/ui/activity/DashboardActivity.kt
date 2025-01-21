package com.anurupjaiswal.learnandachieve.main_package.ui.activity


import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.ActivityDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardActivity : BaseActivity() {
    private val BhartSTA: Boolean = false
    private val premiumIcons = listOf(R.drawable.ic_premium, R.drawable.ic_premium2)
    private val premiumIconChangeInterval = 1_000L
    private var currentIconIndex = 0

    internal lateinit var binding: ActivityDashboardBinding

    private var premiumIconJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        val navController = navHostFragment?.navController

        if (navController == null) {

            Utils.E("NavController is null. Check FragmentContainerView setup.")
            return

        }

        // AppBarConfiguration for top-level destinations

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.home, R.id.PurchasePackage, R.id.mockTest, R.id.account)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)



        binding.shopIcon.setOnClickListener {
            NavigationManager.navigateToFragment(navController, R.id.cartFragment)
        }
   binding.shopBadge.setOnClickListener {
            NavigationManager.navigateToFragment(navController, R.id.cartFragment)
        }





        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.home -> {
                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = false
                    ) // Hide Back Button
                }

                R.id.PurchasePackage, R.id.mockTest, R.id.account -> {
                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button
                }

                R.id.DeleteAccountOtpVerificationFragment -> {
                    setViewsVisibility(
                        isBottomNavVisible = false,
                        isToolbarVisible = false,
                        isBackButtonVisible = false
                    ) // Hide everything
                }

                else -> {
                    setViewsVisibility(
                        isBottomNavVisible = false,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button
                }
            }
        }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    finish()
                }
            }
        })

        if (BhartSTA) {
            startPremiumIconChange()
        }

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun startPremiumIconChange() {
        // Start a coroutine in the main thread
        premiumIconJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                // Update the icon
                binding.ivPremiumCenterIcon.setImageResource(premiumIcons[currentIconIndex])

                // Switch the icon
                currentIconIndex = (currentIconIndex + 1) % premiumIcons.size

                // Delay for the next update (30 seconds)
                delay(premiumIconChangeInterval)
            }
        }
    }


    private fun setViewsVisibility(
        isBottomNavVisible: Boolean,
        isToolbarVisible: Boolean,
        isBackButtonVisible: Boolean
    ) {
        val bottomNavVisibility = if (isBottomNavVisible) View.VISIBLE else View.GONE
        val toolbarVisibility = if (isToolbarVisible) View.VISIBLE else View.GONE
        val backButtonVisibility = if (isBackButtonVisible) View.VISIBLE else View.GONE

        binding.bottomNavigationView.visibility = bottomNavVisibility
        binding.rlToolbar.visibility = toolbarVisibility
        binding.ivBack.visibility = backButtonVisibility
        binding.view.visibility = toolbarVisibility

        val centerIconVisibility = if (isBottomNavVisible && BhartSTA) View.VISIBLE else View.GONE
        binding.mcvCenterIcon.visibility = centerIconVisibility
        binding.rlFloatingCenterIconContainer.visibility = centerIconVisibility

    }


    override fun onDestroy() {
        super.onDestroy()
        premiumIconJob?.cancel()
    }

}
