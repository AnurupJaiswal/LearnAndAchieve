package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.health.connect.datatypes.units.Energy
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentAccountBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.LoginActivity

class AccountFragment : Fragment(R.layout.fragment_account), View.OnClickListener {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAccountBinding.bind(view)




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
                findNavController(), R.id.ourServiceFragment
            )
            binding.rlStudentMaterial -> NavigationManager.navigateToFragment(
                findNavController(), R.id.studyMaterial
            )

            binding.llAboutUs -> NavigationManager.navigateToFragment(
                findNavController(), R.id.aboutUsFragment
            )

            binding.llTermsAndConditions -> NavigationManager.navigateToFragment(
                findNavController(), R.id.termsAndConditionsFragment
            )

            binding.llFAQ -> NavigationManager.navigateToFragment(
                findNavController(), R.id.FAQFragment
            )

            binding.llPrivacyPolicy -> NavigationManager.navigateToFragment(
                findNavController(), R.id.PrivacyPolicyFragment
            )

            binding.llcancellationPolicy -> NavigationManager.navigateToFragment(
                findNavController(), R.id.cancellationPolicyFragment
            )

            binding.rlPurchase -> NavigationManager.navigateToFragment(
                findNavController(), R.id.PurchasePackage
            )

            binding.rlChangePass -> NavigationManager.navigateToFragment(
                findNavController(), R.id.ChangePasswordFragment
            )

            binding.rlJoinAndEarn -> NavigationManager.navigateToFragment(
                findNavController(), R.id.JoinAndEarnFragment
            )

            binding.rlMockTest -> NavigationManager.navigateToFragment(
                findNavController(), R.id.mockTest
            )

            binding.rlOderHistory -> NavigationManager.navigateToFragment(
                findNavController(), R.id.OrderHistoryFragment
            )

            binding.rlProfile -> NavigationManager.navigateToFragment(
                findNavController(), R.id.ProfileDetailsFragment
            )

            binding.rlStudentDetails -> NavigationManager.navigateToFragment(
                findNavController(), R.id.ProfileDetailsFragment
            )

            binding.rlDeleteAccount -> NavigationManager.navigateToFragment(
                findNavController(), R.id.DeleteAccountFragment
            )

            binding.rlBharatSATScholar -> NavigationManager.navigateToFragment(
                findNavController(), R.id.BharatSatScholarShipFragment
            )

            binding.rlBharatSAT -> NavigationManager.navigateToFragment(
                findNavController(), R.id.BharatSatFragment
            )

             binding.lbLogout ->{
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




