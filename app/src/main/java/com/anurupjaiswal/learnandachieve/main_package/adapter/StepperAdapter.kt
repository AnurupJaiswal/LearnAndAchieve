package com.anurupjaiswal.learnandachieve.main_package.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.ContactDetailsFragment
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.OTPVerificationFragment
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.PersonalDetailsFragment

class StepperAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val fragments = listOf(
        PersonalDetailsFragment() as Fragment,
        ContactDetailsFragment() as Fragment,
        OTPVerificationFragment() as Fragment
    )

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}