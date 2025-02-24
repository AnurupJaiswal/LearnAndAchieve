    package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

    import android.app.AlertDialog
    import android.content.Intent
    import android.health.connect.datatypes.units.Energy
    import android.net.Uri
    import android.os.Bundle
    import android.view.View
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.navigation.NavController
    import androidx.navigation.findNavController
    import androidx.navigation.fragment.findNavController
    import com.anurupjaiswal.learnandachieve.R
    import com.anurupjaiswal.learnandachieve.basic.database.User
    import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
    import com.anurupjaiswal.learnandachieve.basic.network.RetryCallback
    import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
    import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
    import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
    import com.anurupjaiswal.learnandachieve.databinding.BottomSheetLogoutBinding
    import com.anurupjaiswal.learnandachieve.databinding.FragmentAccountBinding
    import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
    import com.anurupjaiswal.learnandachieve.main_package.ui.activity.LoginActivity
    import com.anurupjaiswal.learnandachieve.model.ApiResponse
    import com.anurupjaiswal.learnandachieve.model.GetUserResponse
    import com.google.android.material.bottomsheet.BottomSheetDialog
    import com.google.android.material.card.MaterialCardView
    import com.google.gson.Gson
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response


    class AccountFragment : Fragment(R.layout.fragment_account), View.OnClickListener {

        private var _binding: FragmentAccountBinding? = null
        private val binding get() = _binding!!
        private lateinit var navController: NavController
        private var apiService: ApiService?  = null
        private var pradnyaLearningUsername: String?  = null
        private var pradnyaLearningPassword: String?  = null


        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            _binding = FragmentAccountBinding.bind(view)
            init()
        }


        fun init() {

            apiService = RetrofitClient.client
            val dashboardActivity = requireActivity() as DashboardActivity
            navController = dashboardActivity.navController
            binding.tvStudentName.text = "Hey ${Utils.GetSession().firstName}"
            binding.tvStudentClass.text = "Class ${Utils.GetSession().className} (${Utils.GetSession().medium})"
            Utils.Picasso(
                Utils.GetSession().profilePicture.toString(),
                binding.ivProfile,
                R.drawable.ic_profile
            )
            binding.llOurServices.setOnClickListener(this)
            binding.llAboutUs.setOnClickListener(this)
            binding.llTermsAndConditions.setOnClickListener(this)
            binding.llFAQ.setOnClickListener(this)
            binding.llPrivacyPolicy.setOnClickListener(this)
            binding.llcancellationPolicy.setOnClickListener(this)
            binding.rlPurchase.setOnClickListener(this)
            binding.rlChangePass.setOnClickListener(this)
            binding.rlJoinAndEarn.setOnClickListener(this)
            binding.rlMockTest.setOnClickListener(this)
            binding.rlOderHistory.setOnClickListener(this)
            binding.rlProfile.setOnClickListener(this)
            binding.rlStudentDetails.setOnClickListener(this)
            binding.rlDeleteAccount.setOnClickListener(this)
            binding.rlBharatSATScholar.setOnClickListener(this)
            binding.rlBharatSAT.setOnClickListener(this)
            binding.rlStudentMaterial.setOnClickListener(this)
            binding.lbLogout.setOnClickListener(this)
            binding.rlPradnyaTab.setOnClickListener(this)

            Utils.GetSession().token?.let { getUserDetails(it) }
        }

        override fun onClick(view: View) {


            when (view) {
                binding.llOurServices -> NavigationManager.navigateToFragment(
                    navController, R.id.ourServiceFragment
                )

                binding.rlStudentMaterial -> NavigationManager.navigateToFragment(
                    navController, R.id.studyMaterial
                )

                binding.llAboutUs -> NavigationManager.navigateToFragment(
                    navController, R.id.aboutUsFragment
                )

                binding.llTermsAndConditions ->{
                    val bundle = Bundle()
                bundle.putBoolean("isFromAccountFragment", true)
                        NavigationManager.navigateToFragment(
                    navController, R.id.termsAndConditionsFragment
                )
    }
                binding.llFAQ -> NavigationManager.navigateToFragment(
                    navController, R.id.FAQFragment
                )

                binding.llPrivacyPolicy -> NavigationManager.navigateToFragment(
                    navController, R.id.PrivacyPolicyFragment
                )

                binding.llcancellationPolicy -> NavigationManager.navigateToFragment(
                    navController, R.id.cancellationPolicyFragment
                )

                binding.rlPurchase -> NavigationManager.navigateToFragment(
                    navController, R.id.PurchasePackage
                )

                binding.rlChangePass -> NavigationManager.navigateToFragment(
                    navController, R.id.ChangePasswordFragment
                )

                binding.rlJoinAndEarn -> NavigationManager.navigateToFragment(
                    navController, R.id.JoinAndEarnFragment
                )

                binding.rlMockTest -> NavigationManager.navigateToFragment(
                    navController, R.id.mockTest
                )

                binding.rlOderHistory -> NavigationManager.navigateToFragment(
                    navController, R.id.OrderHistoryFragment
                )

                binding.rlProfile -> NavigationManager.navigateToFragment(
                    navController, R.id.ProfileDetailsFragment
                )

                binding.rlStudentDetails -> NavigationManager.navigateToFragment(
                    navController, R.id.ProfileDetailsFragment
                )

                binding.rlDeleteAccount -> NavigationManager.navigateToFragment(
                    navController, R.id.DeleteAccountFragment
                )

                binding.rlBharatSATScholar -> NavigationManager.navigateToFragment(
                    navController, R.id.BharatSatScholarShipFragment
                )

                binding.rlBharatSAT -> NavigationManager.navigateToFragment(
                    navController, R.id.BharatSatFragment
                )

                binding.lbLogout -> {
                    showLogoutBottomSheet()
                }

                 binding.rlPradnyaTab->{
                     val customUri =
                         Uri.parse("pradnyalearning://open?username=$pradnyaLearningUsername&password=$pradnyaLearningPassword")
                     E("$customUri")
                     // Create an intent with the custom URI and target package
                     val intent = Intent(Intent.ACTION_VIEW, customUri).apply {
                         setPackage(Constants.PradnyaLearningPackageName)
                         flags = Intent.FLAG_ACTIVITY_NEW_TASK
                     }

                     // Check if the app can handle the intent
                     if (intent.resolveActivity(requireContext().packageManager) != null) {
                         requireContext().startActivity(intent)
                     } else {
                         // Fallback: if not installed, open the Play Store
                         Utils.openPlayStore(requireContext(), Constants.PradnyaLearningPackageName)
                     }
                 }
            }

        }

        private fun showLogoutBottomSheet() {
            val dialog = BottomSheetDialog(requireContext())

            // Inflate the layout using binding
            val binding = BottomSheetLogoutBinding.inflate(layoutInflater)
            dialog.setContentView(binding.root)
            dialog.setCancelable(false) // Prevent accidental dismissals

            binding.mcvLogout.setOnClickListener {
                logoutUser(binding.loading, binding.mcvLogout, binding.Logout, dialog)
            }

            binding.mcvCancel.setOnClickListener {
                dialog.dismiss() // Dismiss on cancel
            }
            dialog.show()
        }


        private fun getUserDetails(authToken: String) {

            val Token = "Bearer $authToken"

            val call = apiService?.getUserDetails(Token)
            call?.enqueue(RetryCallback(call, callback = object : Callback<GetUserResponse> {
                override fun onResponse(
                    call: Call<GetUserResponse>,
                    response: Response<GetUserResponse>
                ) {
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            val userModel = response.body()

                            if (userModel?.user != null) {
                                val getUser = userModel.user

                                E("User ID: ${getUser.user_id}")
                                E("Full API Response: ${response.body()}")
                                val BharatSAT =
                                    getUser.smartSchoolCredentials?.username?.isNotEmpty() ?: false
                                binding.rlPradnyaTab.visibility =
                                    if (BharatSAT) View.VISIBLE else View.GONE
                                pradnyaLearningUsername = getUser.smartSchoolCredentials.username
                                pradnyaLearningPassword = getUser.smartSchoolCredentials.password
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

                                // Insert or update the user into the local database.
                                UserDataHelper.instance.insertData(userData)
                                E("User data inserted into local database successfully.")
                            } else {
                                // Log or handle the case where 'getUser' is null.
                                E("Error: 'getUser' is null in response.")
                            }
                        } else {
                            // Handle non-OK HTTP responses.
                            handleGetUserResponseApiError(response)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        E("Error processing the request.")
                    }
                }

                override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {

                    t.printStackTrace()

                    E( t.message ?: "Request failed. Please try again later.")
                }
            }))

        }
        private fun handleGetUserResponseApiError(response: Response<GetUserResponse>) {
            when (response.code()) {
                StatusCodeConstant.BAD_REQUEST -> {
                    response.errorBody()?.let { errorBody ->
                        val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                        val displayMessage = message.error ?: "Invalid Request"
                        Utils.E(displayMessage)
                    }
                }

                StatusCodeConstant.UNAUTHORIZED -> {
                    response.errorBody()?.let { errorBody ->
                        val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                        val displayMessage = message.message ?: "Unauthorized Access"
                        Utils.T(requireContext(), displayMessage)
                        Utils.UnAuthorizationToken(requireContext())
                    }
                }

                StatusCodeConstant.NOT_FOUND -> {
                    response.errorBody()?.let { errorBody ->
                        val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                        val displayMessage = message.message ?: "Package not found. Please try again."

                        Utils.E(displayMessage)

                    }
                }

                else -> {
                   E("Unknown error occurred.")

                }
            }
        }

        private fun logoutUser(loading: ProgressBar, logoutButton: MaterialCardView, tvLogout: TextView, dialog: BottomSheetDialog
        ) {
            // Show loading, hide text, disable button
            loading.visibility = View.VISIBLE
            tvLogout.visibility = View.INVISIBLE
            logoutButton.isEnabled = false

            val token = Utils.GetSession().token

            apiService?.logoutUser("Bearer $token")?.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    loading.visibility = View.GONE // Hide loading
                    logoutButton.isEnabled = true  // Re-enable button

                    if (response.isSuccessful) {
                        dialog.dismiss() // Close dialog
                        Utils.UnAuthorizationToken(requireContext()) // Clear token
                        Utils.I_clear(requireContext(), LoginActivity::class.java, null) // Navigate to Login
                    } else {
                        tvLogout.visibility = View.VISIBLE // Restore "Log Out" text
                        Utils.T(requireContext(), "Logout failed! Please try again.")
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    loading.visibility = View.GONE // Hide loading
                    tvLogout.visibility = View.VISIBLE // Restore text
                    logoutButton.isEnabled = true  // Re-enable button

                    Utils.T(requireContext(), "Network error: ${t.localizedMessage}")
                }
            })
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }




