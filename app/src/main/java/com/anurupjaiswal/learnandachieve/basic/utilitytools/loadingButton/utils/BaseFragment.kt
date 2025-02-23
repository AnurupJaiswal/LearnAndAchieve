package com.anurupjaiswal.learnandachieve.basic.utilitytools.loadingButton.utils

import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.network.ConnectionStatus
import com.anurupjaiswal.learnandachieve.basic.network.ConnectivityLiveData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView


open class BaseFragment : Fragment() {

    protected lateinit var connectivityLiveData: ConnectivityLiveData
    private var bottomSheetDialog: BottomSheetDialog? = null

    override fun onViewCreated(view: View, savedInstanceState: android.os.Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        connectivityLiveData = ConnectivityLiveData(requireContext())
        connectivityLiveData.observe(viewLifecycleOwner) { status ->
            when (status) {
                ConnectionStatus.AVAILABLE -> bottomSheetDialog?.dismiss()
                ConnectionStatus.LOST -> showNoInternetBottomSheet()
            }
        }
    }

    /**
     * Displays a non-cancelable BottomSheet with a Retry button when there is no internet.
     */
    private fun showNoInternetBottomSheet() {
        // Avoid showing multiple dialogs
        if (bottomSheetDialog?.isShowing == true) return

        bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(layoutInflater.inflate(R.layout.bottom_sheet_internet, null))
            setCancelable(false)
        }

        bottomSheetDialog?.findViewById<MaterialCardView>(R.id.mcvRetry)?.setOnClickListener {
            if (connectivityLiveData.value == ConnectionStatus.AVAILABLE) {
                bottomSheetDialog?.dismiss()
            } else {
                Toast.makeText(requireContext(), "Still no internet connection", Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetDialog?.show()
    }
}