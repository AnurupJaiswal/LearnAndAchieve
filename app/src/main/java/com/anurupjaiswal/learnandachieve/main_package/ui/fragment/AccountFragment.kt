package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.health.connect.datatypes.units.Energy
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentAccountBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.LoginActivity

class AccountFragment : Fragment(R.layout.fragment_account), View.OnClickListener {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAccountBinding.bind(view)
        init()
    }


    fun init() {
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
        Utils.showLogoutDialog(
            context = requireContext(),
            onLogoutConfirmed = {

                Utils.UnAuthorizationToken(requireContext())
                Utils.I_clear(requireContext(), LoginActivity::class.java, null)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




