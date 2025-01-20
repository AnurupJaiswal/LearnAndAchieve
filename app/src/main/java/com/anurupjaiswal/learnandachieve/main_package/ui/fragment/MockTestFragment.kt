package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentMockTestBinding

import com.anurupjaiswal.learnandachieve.main_package.adapter.MockTestAdapter
import com.anurupjaiswal.learnandachieve.model.MockTest




class MockTestFragment : Fragment() {

    private var _binding: FragmentMockTestBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MockTestAdapter
    private val mockTestList = listOf(
        MockTest(
            title = "Bharat SAT Exam 3 Mock Test",
            subjects = "English, Hindi, History, and more",
            totalQuestions = "100",
            time = "45 mins",
            noOfAttempts = "3",
            note = "Students will have access within 30 days of registration."
        ),
        MockTest(
            title = "Bharat SAT Exam  & 3 Mock Test",
            subjects = "English, Hindi, History",
            totalQuestions = "100",
            time = "50 mins",
            noOfAttempts = "4",
            note = "Students will have access within 40 days of registration."
        ),
        MockTest(
            title = "Bharat SAT Exam  & 3 Mock Test",
            subjects = "English, Hindi, History",
            totalQuestions = "100",
            time = "50 mins",
            noOfAttempts = "4",
            note = "Students will have access within 40 days of registration."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMockTestBinding.inflate(inflater, container, false)
        setuprvMockTest()
        return binding.root
    }

    private fun setuprvMockTest() {
        adapter = MockTestAdapter(mockTestList) { mockTest ->


            NavigationManager.navigateToFragment(
                findNavController(),
                R.id.QuestionComparisonFragment
            )


        }
        binding.rvMockTest.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMockTest.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}

