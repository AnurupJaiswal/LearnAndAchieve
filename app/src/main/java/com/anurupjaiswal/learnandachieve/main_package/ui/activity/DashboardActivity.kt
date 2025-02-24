package com.anurupjaiswal.learnandachieve.main_package.ui.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.User
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.ActivityDashboardBinding
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.google.gson.Gson
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : BaseActivity(), PaymentResultListener {
    private var BharatSAT: Boolean = false
    private val premiumIcons = listOf(R.drawable.ic_premium, R.drawable.ic_premium_white)
    private val premiumIconChangeInterval = 3_000L
    private var currentIconIndex = 0
    private var cartCount = 0
    private lateinit var apiService: ApiService
    private var pradnyaLearningUsername: String? = null
    private var pradnyaLearningPassword: String? = null
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
        navController = navHostFragment?.navController
            ?: throw IllegalStateException("NavController not found!")


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

        Token = Utils.GetSession().token
        val authToken = "Bearer $Token"
        getCartAllCount(authToken)
        Token?.let { getUserDetails(it) }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {


                R.id.home -> {

                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = false
                    ) // Hide Back Button
                    window?.statusBarColor =
                        ContextCompat.getColor(this, R.color.white) // Default white color

                }

                R.id.account -> {


                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button

                    window?.statusBarColor =
                        ContextCompat.getColor(this, R.color.white) // Default white color

                }

                R.id.PurchasePackage, R.id.mockTest -> {

                    setViewsVisibility(
                        isBottomNavVisible = true,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button

                    window?.statusBarColor =
                        ContextCompat.getColor(this, R.color.white) // Default white color


                }

                R.id.DeleteAccountOtpVerificationFragment -> {
                    setViewsVisibility(
                        isBottomNavVisible = false,
                        isToolbarVisible = false,
                        isBackButtonVisible = false
                    ) // Hide everything

                    window?.statusBarColor = ContextCompat.getColor(
                        this,
                        R.color.primaryColor
                    ) // Specific color for this fragment

                }

                else -> {
                    setViewsVisibility(
                        isBottomNavVisible = false,
                        isToolbarVisible = true,
                        isBackButtonVisible = true
                    ) // Show Back Button

                    window?.statusBarColor =
                        ContextCompat.getColor(this, R.color.white) // Default white color

                }
            }
        }
        updateCartBadge()






        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.BharatSatSubmitSuccessFragment) {
                    // Navigate directly to Home Fragment
                    navController.popBackStack(R.id.home, false)
                } else {
                    // For other fragments, try to navigate up; if not possible, finish the activity.
                    if (!navController.navigateUp()) {
                        finish()
                    }
                }
            }
        })
//
//         binding.ivPremiumCenterIcon.setOnClickListener{
//             Utils.openAppOrPlayStore(this, Constants.PradnyaLearningPackageName,pradnyaLearningUsername!!,pradnyaLearningUsername!!)
//         }


        binding.ivPremiumCenterIcon.setOnClickListener {
            val customUri =
                Uri.parse("pradnyalearning://open?username=$pradnyaLearningUsername&password=$pradnyaLearningPassword")
            E("$customUri")
            // Create an intent with the custom URI and target package
            val intent = Intent(Intent.ACTION_VIEW, customUri).apply {
                setPackage(Constants.PradnyaLearningPackageName)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            // Check if the app can handle the intent
            if (intent.resolveActivity(this.packageManager) != null) {
                this.startActivity(intent)
            } else {
                // Fallback: if not installed, open the Play Store
                Utils.openPlayStore(this, Constants.PradnyaLearningPackageName)
            }
        }



        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    if (navController.currentDestination?.id != R.id.home) {
                        navController.navigate(
                            R.id.home,
                            null,
                            navOptions {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        )
                    }
                    true
                }

                R.id.mockTest -> {
                    if (navController.currentDestination?.id != R.id.mockTest) {
                        navController.navigate(
                            R.id.mockTest,
                            null,
                            navOptions {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        )
                    }
                    true
                }

                R.id.PurchasePackage -> {
                    if (navController.currentDestination?.id != R.id.PurchasePackage) {
                        navController.navigate(
                            R.id.PurchasePackage,
                            null,
                            navOptions {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        )
                    }
                    true
                }

                R.id.account -> {
                    // If not already on AccountFragment, navigate to it;
                    // otherwise, if already there, pop any deeper fragments.
                    if (navController.currentDestination?.id != R.id.account) {
                        navController.navigate(
                            R.id.account,
                            null,
                            navOptions {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                                launchSingleTop = true
                            }
                        )
                    } else {
                        navController.popBackStack(R.id.account, false)
                    }
                    true
                }

                else -> false
            }
        }

    }


    private fun startPremiumIconChange() {
        premiumIconJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                // Fade out animation
                binding.ivPremiumCenterIcon.animate()
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction {
                        // Update the image resource when fade-out is complete
                        binding.ivPremiumCenterIcon.setImageResource(premiumIcons[currentIconIndex])

                        // Fade in animation
                        binding.ivPremiumCenterIcon.animate()
                            .alpha(1f)
                            .setDuration(0)
                            .start()
                    }
                    .start()

                // Update the current icon index for the next iteration
                currentIconIndex = (currentIconIndex + 1) % premiumIcons.size

                // Wait for the specified interval before the next change
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

        val centerIconVisibility = if (isBottomNavVisible && BharatSAT) View.VISIBLE else View.GONE
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


    fun getCartAllCount(authToken: String) {


        apiService.getCartData(authToken).enqueue(object : Callback<AllCartResponse> {
            override fun onResponse(
                call: Call<AllCartResponse>,
                response: Response<AllCartResponse>
            ) {
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
                                    errorMessage =
                                        apiError.message ?: apiError.error ?: "Unknown error"
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


    override fun onPaymentSuccess(razorpayPaymentId: String) {
        // Forward callback to the current fragment if it implements PaymentResultListener.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
        if (currentFragment is PaymentResultListener) {
            currentFragment.onPaymentSuccess(razorpayPaymentId)
        }
    }

    override fun onPaymentError(code: Int, description: String) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
        if (currentFragment is PaymentResultListener) {
            currentFragment.onPaymentError(code, description)
        }
    }

    private fun getUserDetails(authToken: String) {

        val Token = "Bearer $authToken"

        apiService?.getUserDetails(Token)?.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val userModel = response.body()

                        if (userModel?.user != null) {
                            val getUser = userModel.user
                            pradnyaLearningUsername = getUser.smartSchoolCredentials.username
                            pradnyaLearningPassword = getUser.smartSchoolCredentials.password
                            E("Anurup's pradnyaLearningUsername:$pradnyaLearningUsername")
                            E("Anurup's pradnyaLearningPassword:$pradnyaLearningPassword")
                            E("pradnyaLearningUsername:${getUser.smartSchoolCredentials.username}")
                            E("pradnyaLearningPassword: ${getUser.smartSchoolCredentials.password}")
                            E("Full API Response: ${response.body()}")
                            BharatSAT =
                                getUser.smartSchoolCredentials?.username?.isNotEmpty() ?: false
                            if (BharatSAT && premiumIconJob == null) {
                                startPremiumIconChange()
                            }
                            updatePremiumIconVisibility()

                            val userData = User().apply {
                                _id = getUser.user_id
                                token = authToken
                                firstName = getUser.firstName
                                middleName = getUser.middleName
                                lastName = getUser.lastName
                                dateOfBirth = getUser.dateOfBirth
                                gender = getUser.gender
                                schoolName = getUser.schoolName
                                medium = getUser.medium
                                classId = getUser.class_id
                                profilePicture = getUser.profilePicture
                                registerBy = getUser.registerBy
                                referralCode = getUser.referralCode
                                email = getUser.email
                                mobile = getUser.mobile
                                addressLineOne = getUser.addressLineOne
                                addressLineTwo = getUser.addressLineTwo
                                state = getUser.state
                                district = getUser.district
                                taluka = getUser.taluka
                                createdDate = getUser.created_date
                                updatedDate = getUser.updated_date
                                className = getUser.class_name
                            }

                            // Insert or update into the local database
                            UserDataHelper.instance.insertData(userData)
                            E("User data inserted into local database successfully.")
                        } else {
                            // Log or handle case where 'getUser' is null
                            E("Error: 'getUser' is null in response.")
                        }

                    } else {
                        handleGetUserResponseApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(this@DashboardActivity, "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                t.printStackTrace()
                Utils.T(
                    this@DashboardActivity,
                    t.message ?: "Request failed. Please try again later."
                )
            }
        })


    }

    private fun handleGetUserResponseApiError(response: Response<GetUserResponse>) {
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.error ?: "Invalid Request"
                    Utils.T(this@DashboardActivity, displayMessage)
                }
            }

            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Unauthorized Access"
                    Utils.T(this@DashboardActivity, displayMessage)
                    Utils.UnAuthorizationToken(this@DashboardActivity)
                }
            }

            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Package not found. Please try again."
                    Utils.T(this@DashboardActivity, displayMessage)
                }
            }

            else -> {
                Utils.T(this@DashboardActivity, "Unknown error occurred.")

            }
        }
    }

    private fun updatePremiumIconVisibility() {
        val isBottomNavVisible = binding.bottomNavigationView.visibility == View.VISIBLE
        val visibility = if (isBottomNavVisible && BharatSAT) View.VISIBLE else View.GONE

        binding.mcvCenterIcon.visibility = visibility
        binding.rlFloatingCenterIconContainer.visibility = visibility
    }

}