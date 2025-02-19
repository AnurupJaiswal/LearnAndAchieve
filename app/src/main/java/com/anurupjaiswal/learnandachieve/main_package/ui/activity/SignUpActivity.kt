package com.anurupjaiswal.learnandachieve.main_package.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.BaseActivity
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.toggleProgressBarAndText
import com.anurupjaiswal.learnandachieve.databinding.ActivitySignUpBinding
import com.anurupjaiswal.learnandachieve.databinding.CustomStepperBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.ContactDetailsFragment
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.OTPVerificationFragment
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.PersonalDetailsFragment




class SignUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var stepperBinding: CustomStepperBinding
    private var personalDetailsBundle: Bundle? = null

    private val dotResources = listOf(
        R.drawable.dot_background_orange,
        R.drawable.dot_background_gray
    )
    private val lineResources = listOf(
        R.drawable.line_background_orange,
        R.drawable.line_background_gray
    )
    private val textColorResources = listOf(
        R.color.primaryColor,
        R.color.gray
    )

    private var currentFragmentIndex = 0
    private val fragments = listOf(
        PersonalDetailsFragment() as Fragment,
        ContactDetailsFragment()as Fragment,
        OTPVerificationFragment() as Fragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        stepperBinding = binding.customStepper

        // Load the initial fragment
        loadFragment(fragments[currentFragmentIndex])
        updateButtonLabel()

        // Set up the "Next" button click listener
        binding.mcvNext.setOnClickListener { handleNextButtonClick() }
        binding.rlHaveAccount.setOnClickListener {
              finish()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun handleNextButtonClick() {
        val currentFragment = fragments[currentFragmentIndex]
        when (currentFragment) {
            is PersonalDetailsFragment -> {
                if (currentFragment.validateFields()) {
                    personalDetailsBundle = currentFragment.getPersonalDetailsBundle()

                    personalDetailsBundle?.keySet()?.forEach { key ->
                        val value = personalDetailsBundle?.get(key)
                        E("SignUpActivity Bundle Key: $key, Value: $value")
                    }

                    goToNextFragment(personalDetailsBundle)
                }
            }
            is ContactDetailsFragment -> {
                if (currentFragment.validateFields()) {
                    // Show progress bar and hide the text view
                    toggleProgressBarAndText(true, binding.loading, binding.tvNext,binding.root)

                    // Call the API and check the response
                    currentFragment.callApi { isSuccess, contactDetailsBundle ->
                        // Hide the progress bar and show the text view after the API response
                        toggleProgressBarAndText(false, binding.loading, binding.tvNext,binding.root)

                        if (isSuccess) {
                            // Combine personalDetailsBundle and contactDetailsBundle
                            val combinedBundle = Bundle().apply {
                                // Add all data from personalDetailsBundle if it's not null
                                personalDetailsBundle?.keySet()?.forEach { key ->
                                    putString(key, personalDetailsBundle?.getString(key))
                                }
                                // Add all data from contactDetailsBundle
                                contactDetailsBundle?.keySet()?.forEach { key ->
                                    putString(key, contactDetailsBundle.getString(key))
                                }
                            }

                            // Pass the combinedBundle containing both personal and contact details
                            goToNextFragment(combinedBundle)
                        } else {
                         //   Utils.T(this, "Please try again.")
                        }
                    }
                }

        }    is OTPVerificationFragment -> {
            if (currentFragment.validateFields()) {
                // Show progress bar while verifying OTP
                toggleProgressBarAndText(true, binding.loading, binding.tvNext,binding.root)

                // Call the API for OTP verification
                currentFragment.callApi { isSuccess ->
                    toggleProgressBarAndText(false, binding.loading, binding.tvNext,binding.root)

                    if (isSuccess) {
                        Utils.T(this, "OTP verified successfully")
                        Utils.I_clear(this, LoginActivity::class.java, null)
                    }
                }
                }
            }
        }
    }



    private fun goToNextFragment(bundle: Bundle? = null) {
        if (currentFragmentIndex < fragments.size - 1) {
            currentFragmentIndex++

            val nextFragment = fragments[currentFragmentIndex]

            nextFragment.arguments = bundle
            // Log the data inside the bundle
            bundle?.keySet()?.forEach { key ->
                val value = bundle.get(key)
                Log.d("SignUpActivity", "Passing Data - Key: $key, Value: $value")
            }



            if (bundle != null) {
                nextFragment.arguments = bundle
            }

            loadFragment(nextFragment)
            updateStepper(currentFragmentIndex)
            updateButtonLabel()
        } else {
            Utils.I_clear(this, DashboardActivity::class.java, null)
        }
    }


    private fun updateButtonLabel() {
        binding.tvNext.text = getString(
            if (currentFragmentIndex == fragments.size - 1) R.string.submit
            else R.string.next
        )
    }

    private fun updateStepper(position: Int) {
        val stepDots = listOf(
            stepperBinding.step1Dot,
            stepperBinding.step2Dot,
            stepperBinding.step3Dot
        )
        val stepLines = listOf(
            stepperBinding.step1Line,
            stepperBinding.step2Line
        )
        val stepTexts = listOf(
            stepperBinding.tvPersonalDetails,
            stepperBinding.tvContactDetails,
            stepperBinding.tvOtpVerification
        )
      //  You can access your mock test from February 15, 2025, onwards.

        stepDots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(if (index <= position) dotResources[0] else dotResources[1])
        }
        stepLines.forEachIndexed { index, line ->
            line.setBackgroundResource(if (index < position) lineResources[0] else lineResources[1])
        }
        stepTexts.forEachIndexed { index, textView ->
            textView.setTextColor(
                resources.getColor(
                    if (index <= position) textColorResources[0] else textColorResources[1],
                    theme
                )
            )
        }
    }



    override fun onBackPressed() {
        if (currentFragmentIndex > 0) {
            currentFragmentIndex--
            loadFragment(fragments[currentFragmentIndex])
            updateStepper(currentFragmentIndex)
            updateButtonLabel()
        } else {
            super.onBackPressed()
        }
    }
}
