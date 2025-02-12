package com.anurupjaiswal.learnandachieve.main_package.ui.activity


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.ActivityDashboardBinding
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : BaseActivity() {
    private val BhartSTA: Boolean = false
    private val premiumIcons = listOf(R.drawable.ic_premium, R.drawable.ic_premium2)
    private val premiumIconChangeInterval = 1_000L
    private var currentIconIndex = 0
    private var cartCount = 0 // Directly store the cart count here
    private lateinit var apiService: ApiService
    var Token: String? = null

    lateinit var navController: NavController

    internal lateinit var binding: ActivityDashboardBinding

    private var premiumIconJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        apiService = RetrofitClient.client
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        navController = navHostFragment?.navController ?: throw IllegalStateException("NavController not found!")



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
        getCartAllCount()




        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {



                R.id.home -> {

                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = false
                    ) // Hide Back Button
                    window?.statusBarColor = ContextCompat.getColor(this, R.color.white) // Default white color

                }

                 R.id.account -> {
                     navController.popBackStack(R.id.account,false)

                     NavigationManager.navigateToFragment(navController,R.id.account)

                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button

                     window?.statusBarColor = ContextCompat.getColor(this, R.color.white) // Default white color

                }

                R.id.PurchasePackage, R.id.mockTest, -> {

                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button

                    window?.statusBarColor = ContextCompat.getColor(this, R.color.white) // Default white color


                }

                R.id.DeleteAccountOtpVerificationFragment -> {
                    setViewsVisibility(
                        isBottomNavVisible = false,
                        isToolbarVisible = false,
                        isBackButtonVisible = false
                    ) // Hide everything

                    window?.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor) // Specific color for this fragment

                }

                else -> {
                    setViewsVisibility(
                        isBottomNavVisible = false,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button

                    window?.statusBarColor = ContextCompat.getColor(this, R.color.white) // Default white color

                }
            }
        }
        updateCartBadge()


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

    @SuppressLint("SetTextI18n")
    private fun updateCartBadge() {
        Log.d("CartDebug", "Updating cart badge with count: $cartCount")

        binding.shopBadge.text = cartCount.toString()

        if (cartCount > 0) {
            binding.shopBadge.visibility = View.VISIBLE
        } else {
            binding.shopBadge.visibility = View.GONE
        }
    }

    fun updateCartCount(count: Int) {
        Log.d("CartDebug", "Cart count updated to: $cartCount")

        cartCount = count
        updateCartBadge()
    }


    fun getCartAllCount() {

        Token = Utils.GetSession().token
        val authToken = "Bearer $Token"

        apiService.getCartData(authToken).enqueue(object : Callback<AllCartResponse> {
            override fun onResponse(call: Call<AllCartResponse>, response: Response<AllCartResponse>) {
                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            val allCartResponse = response.body()
                            updateCartCount(allCartResponse!!.cartCount)
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.UnAuthorizationToken(this@DashboardActivity)
                        }
                        else -> {
                            val errorBody = response.errorBody()?.string()
                            var errorMessage = "Unknown error"
                            if (!errorBody.isNullOrEmpty()) {
                                try {
                                    val gson = Gson()
                                    val apiError = gson.fromJson(errorBody, APIError::class.java)
                                    errorMessage = apiError.message ?: apiError.error ?: "Unknown error"
                                } catch (e: Exception) {
                                    errorMessage = errorBody
                                }
                            }
                            E("$errorMessage")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<AllCartResponse>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
            }
        })
    }




}