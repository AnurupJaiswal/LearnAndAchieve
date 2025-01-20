package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentPrivacyPolicyBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.PrivacyPolicyAdapter
import com.anurupjaiswal.learnandachieve.model.PrivacyPolicyItem

class PrivacyPolicyFragment : Fragment() {

    private var _binding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = _binding!!

    // Sample data for privacy policy (now directly initialized)
    private val privacyPolicyItems = listOf(
        PrivacyPolicyItem(
            title = "1. Information Provided During Registration",
            description = "We collect and store the information provided during the registration process to create and manage user accounts, ensuring a personalized experience for our users."
        ),
        PrivacyPolicyItem(
            title = "2. Photos and Testimony",
            description = "Users may voluntarily provide photos and testimonies, which may be used for promotional purposes with explicit consent."
        ),
        PrivacyPolicyItem(
            title = "3. Log Information",
            description = "Our systems automatically collect log information, including IP addresses, browser types, and pages visited, to analyze trends, administer the site, and gather demographic information for overall user experience improvement."
        )
    )

    // onCreateView to inflate the layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    // onViewCreated is where RecyclerView setup takes place
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    // Set up the RecyclerView and Adapter
    private fun setupRecyclerView() {
        binding.rvPrivacyPolicy.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPrivacyPolicy.adapter = PrivacyPolicyAdapter(privacyPolicyItems)
    }

    // Clean up binding reference to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
