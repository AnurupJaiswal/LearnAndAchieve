    package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

    import android.app.AlertDialog
    import android.health.connect.datatypes.units.Energy
    import android.os.Bundle
    import android.view.View
    import android.widget.ProgressBar
    import android.widget.TextView
    import androidx.fragment.app.Fragment
    import androidx.navigation.NavController
    import androidx.navigation.findNavController
    import androidx.navigation.fragment.findNavController
    import com.anurupjaiswal.learnandachieve.R
    import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
    import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
    import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
    import com.anurupjaiswal.learnandachieve.databinding.DialogLogoutBinding
    import com.anurupjaiswal.learnandachieve.databinding.FragmentAccountBinding
    import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
    import com.anurupjaiswal.learnandachieve.main_package.ui.activity.LoginActivity
    import com.anurupjaiswal.learnandachieve.model.ApiResponse
    import com.google.android.material.card.MaterialCardView
    import retrofit2.Call
    import retrofit2.Callback
    import retrofit2.Response


    class AccountFragment : Fragment(R.layout.fragment_account), View.OnClickListener {

        private var _binding: FragmentAccountBinding? = null
        private val binding get() = _binding!!
        private lateinit var navController: NavController
        private var apiService: ApiService?  = null

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
                    showLogoutDialog()
                }
            }

        }

        private fun showLogoutDialog() {
            val binding = DialogLogoutBinding.inflate(layoutInflater)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(binding.root)
                .setCancelable(false) // Prevent accidental dismissals
                .create()

            binding.mcvLogout.setOnClickListener {
                logoutUser(
                    binding.loading,   // Pass ProgressBar
                    binding.mcvLogout, // Pass Button
                    binding.tvLogout,  // Pass "Log Out" Text
                    dialog             // Pass Dialog
                )
            }

            binding.mcvCancel.setOnClickListener {
                dialog.dismiss() // Dismiss on cancel
            }

            dialog.show()
        }


        private fun logoutUser(
            loading: ProgressBar,
            logoutButton: MaterialCardView,
            tvLogout: TextView,
            dialog: AlertDialog
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




