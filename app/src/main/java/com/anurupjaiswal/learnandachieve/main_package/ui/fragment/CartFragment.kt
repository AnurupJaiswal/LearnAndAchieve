package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentCartBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.model.PackageModel

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val packageList = mutableListOf<PackageModel>()

    private var subtotal: Int = 0
    private var totalDiscount: Int = 0
    private var total: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupSampleData()
        calculatePrices()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)


        binding.lbProceed.setOnClickListener {

            NavigationManager.navigateToFragment(
                findNavController(), R.id.CheckoutFragment
            )


        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        updateSummary()
    }

    private fun setupSampleData() {
        packageList.apply {
            add(
                PackageModel(
                    title = "BHARAT SAT EXAM & 3 Mock Test",
                    price = "₹1359", // Discounted price
                    originalPrice = "₹1599", // Original price
                    imageRes = R.drawable.book
                )
            )
            add(
                PackageModel(
                    title = "UPSC Preparation Kit",
                    price = "₹1999", // Discounted price
                    originalPrice = "₹2499", // Original price
                    imageRes = R.drawable.book
                )
            )
        }
    }

    private fun setupRecyclerView() {
        val cartAdapter = CartAdapter(packageList) { position ->
            removePackage(position)
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun removePackage(position: Int) {
        if (position in packageList.indices) {
            packageList.removeAt(position)
            binding.recyclerViewCart.adapter?.notifyItemRemoved(position)
            calculatePrices()
            updateSummary()
            if (packageList.isEmpty()) navigateBack()
        }
    }

    private fun calculatePrices() {
        subtotal = packageList.sumOf { it.originalPrice.removePrefix("₹").toInt() }
        total = packageList.sumOf { it.price.removePrefix("₹").toInt() }
        totalDiscount = subtotal - total
    }

    private fun updateSummary() {
        binding.tvSubtotal.text = getString(R.string.price_format, subtotal)
        binding.tvDiscount.text = getString(R.string.discount_format, totalDiscount)
        binding.tvTotal.text = getString(R.string.price_format, total)
    }

    private fun navigateBack() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
