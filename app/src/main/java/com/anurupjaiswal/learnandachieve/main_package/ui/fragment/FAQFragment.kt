package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.databinding.FragmentFAQBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.FAQCategoryAdapter
import com.anurupjaiswal.learnandachieve.model.FAQCategory
import com.anurupjaiswal.learnandachieve.model.FAQQuestion

class FAQFragment : Fragment() {

    private var _binding: FragmentFAQBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFAQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Fetch the data
        val faqData = getSampleFAQData()

        // Set up the RecyclerView
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = FAQCategoryAdapter(faqData)
        }
    }

    private fun getSampleFAQData(): List<FAQCategory> {
        return listOf(
            FAQCategory(
                "General",
                listOf(
                    FAQQuestion(
                        question = "What is Learn and Achieve Edutech Project?",
                        answer = "Learn and Achieve is an educational initiative aimed at providing innovative learning solutions to students across India."
                    ),
                    FAQQuestion(
                        question = " how does it benefit students?",
                        answer = "Skill Enhancement: Participating in BHARAT SAT encourages students to develop a broad spectrum of skills, from problem-solving to time management, fostering their holistic growth."
                    )
                )
            ),
            FAQCategory(
                "Bharat SAT",
                listOf(
                    FAQQuestion(
                        question = "What is Bharat SAT?",
                        answer = "Bharat SAT is a state-of-the-art satellite service for educational purposes."
                    ),
                    FAQQuestion(
                        question = "How to access Bharat SAT?",
                        answer = "Access Bharat SAT with a subscription tailored for your needs."
                    )
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
