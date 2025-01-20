package com.anurupjaiswal.learnandachieve.main_package.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.ContactDetailsFragment
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.OTPVerificationFragment
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.PersonalDetailsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3 // Three pages (Personal, Contact, OTP)
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PersonalDetailsFragment()
            1 -> ContactDetailsFragment()
            else -> OTPVerificationFragment()
        }
    }
}
