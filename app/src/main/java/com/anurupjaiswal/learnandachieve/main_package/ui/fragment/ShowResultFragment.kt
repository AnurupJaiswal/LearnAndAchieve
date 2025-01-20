package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R


import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentShowResultBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ShowResultAdapter
import com.anurupjaiswal.learnandachieve.model.ShowResult

class ShowResultFragment : Fragment() {

    private var _binding: FragmentShowResultBinding? = null
    private val binding get() = _binding!!

    private val results = listOf(
        ShowResult("Jul 4, 2024", "BHARAT SAT EXAM & 3 Mock Test", "80/100"),
        ShowResult("Jul 4, 2024", "BHARAT SAT EXAM & 10 MockTest& Study Material", "80/100"),
        ShowResult("Jul 4, 2024", "BHARAT SAT EXAM & 3 Mock Test", "80/100")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowResultBinding.inflate(inflater, container, false)
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val adapter = ShowResultAdapter(results) { result ->

            NavigationManager.navigateToFragment(findNavController(),R.id.PerformanceSummaryFragment)
        }
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
