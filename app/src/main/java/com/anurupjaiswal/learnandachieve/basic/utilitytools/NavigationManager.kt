package com.anurupjaiswal.learnandachieve.basic.utilitytools

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.anurupjaiswal.learnandachieve.R

object NavigationManager {



    fun navigateToFragment(
        navController: NavController,
        destinationId: Int,
        bundle: Bundle? = null // Default is null if no data is passed
    ) {
        // Check if the current destination is the same as the target destination
        if (navController.currentDestination?.id == destinationId) {
            // Do nothing as the fragment is already loaded
            return
        }


        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)  // Reverse transition for pop
            .setPopExitAnim(R.anim.slide_out_right) // Reverse transition for pop
            .build()
        // Proceed with navigation if the destination is different
        navController.navigate(destinationId, bundle, navOptions)
    }




}