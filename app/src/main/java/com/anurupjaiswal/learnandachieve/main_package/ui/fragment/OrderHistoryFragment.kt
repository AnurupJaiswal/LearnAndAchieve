package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentOrderHistoryBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.OrderHistoryAdapter
import com.anurupjaiswal.learnandachieve.model.OrderHistoryItem

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OrderHistoryAdapter

    private val orderHistoryItems = listOf(
        OrderHistoryItem(
            "Jul 1, 2024 02:14",
            "BHARAT SAT EXAM & 10 MockTest & Study Material",
            "123456789",
            "ABGHSDFJBJKNKF",
            "1399.00"
        ), OrderHistoryItem(
            "Jul 2, 2024 02:14",
            "BHARAT SAT EXAM & 3 Mock Test",
            "54555554444",
            "RREEDEDEFGVFFD",
            "499.00"
        ), OrderHistoryItem(
            "Jul 3, 2024 02:14", "Detailed performance analysis for all tests",
            "2323232223",
            "MNBSFGHJ56788",
            "7559.00"
        ), OrderHistoryItem(
            "Jul 4, 2024 02:14",
            "MH SET Official Paper 2",
            "49504844434",
            "VDEDDSFGHJ56788",
            "560.00"
        ), OrderHistoryItem(
            "Jul 5, 2024 02:14", "MH SET Paper 1", "987654454545", "AAAASFGHJ56788", "799.00"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the view using ViewBinding
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)

        setupRecyclerView()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvOrderHistory.layoutManager = LinearLayoutManager(requireContext())
        adapter = OrderHistoryAdapter(orderHistoryItems)
        binding.rvOrderHistory.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
