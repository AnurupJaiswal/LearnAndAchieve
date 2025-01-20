package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentPurchasePackageBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.PurchasePackageAdapter
import com.anurupjaiswal.learnandachieve.model.PackageModel

class PurchasePackageFragment : Fragment() {

    private var _binding: FragmentPurchasePackageBinding? = null
    private val binding get() = _binding!!

    // Sample data for the packages
    private val packageList = listOf(
        PackageModel("BHARAT SAT EXAM & 3 Mock Test", "1359", "1599", R.drawable.colosseum, "Build 16 web development projects for your portfolio, ready to apply for junior developer jobs."),
        PackageModel("BHARAT SAT EXAM & 5 Mock Test", "1459", "1759", R.drawable.ic_student_next_gen, "Build 16 web development projects for your portfolio, ready to apply for junior developer jobs.")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasePackageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PurchasePackageAdapter(packageList)
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
