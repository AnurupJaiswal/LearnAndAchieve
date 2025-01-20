package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.databinding.FragmentDeleteAccountOtpVerificationBinding

class DeleteAccountOtpVerificationFragment : Fragment() {

    private var _binding: FragmentDeleteAccountOtpVerificationBinding? = null
    private val binding get() = _binding!!

    // Inflate the fragment layout and initialize the binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using the binding
        _binding = FragmentDeleteAccountOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Use binding to interact with the views and control visibility
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.McvBack.setOnClickListener {
            findNavController().navigateUp()

        }


    }

    // Clean up binding in onDestroyView to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()


        // Set binding to null to prevent memory leaks
        _binding = null
    }
}
