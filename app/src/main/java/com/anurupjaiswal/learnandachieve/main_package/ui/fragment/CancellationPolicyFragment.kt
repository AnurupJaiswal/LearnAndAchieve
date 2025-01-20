package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentCancellationPolicyBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.PrivacyPolicyAdapter
import com.anurupjaiswal.learnandachieve.model.PrivacyPolicyItem

class CancellationPolicyFragment : Fragment() {

    private var _binding: FragmentCancellationPolicyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancellationPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(getCancellationPolicyItems())
    }

    private fun setupRecyclerView(items: List<PrivacyPolicyItem>) {
        val adapter = PrivacyPolicyAdapter(items)
        binding.rvCancellationPolicy.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCancellationPolicy.adapter = adapter
    }

    private fun getCancellationPolicyItems(): List<PrivacyPolicyItem> {
        return listOf(
            PrivacyPolicyItem(
                title = "1. Refund Eligibility",
                description = "Cancellations made within 24 hours of booking are eligible for a full refund, provided the service has not been utilized."
            ),
            PrivacyPolicyItem(
                title = "2. Non-Refundable Services",
                description = "Certain promotional offers and discounted services are non-refundable and non-cancellable."
            ),
            PrivacyPolicyItem(
                title = "3. Late Cancellations",
                description = "Cancellations made less than 24 hours before the service may incur a cancellation fee."
            ),
            PrivacyPolicyItem(
                title = "4. Modification Policy",
                description = "Modifications to bookings can be made free of charge up to 48 hours before the scheduled service."
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
