package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentOurServiceBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.OurServiceAdapter
import com.anurupjaiswal.learnandachieve.model.ServiceItem

class OurServiceFragment : Fragment(R.layout.fragment_our_service) {

    private var _binding: FragmentOurServiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OurServiceAdapter

    private val services: List<ServiceItem> by lazy {
        listOf(
            ServiceItem(
                title = "Empowering Students with Free E-Learning Services",
                description = "At Learn and Achieve, our mission is to empower students, parents, and educators with free services that foster learning and growth. We understand the importance of quality education, and to that end, we offer a range of services to support 5th to 10th class students. Our YouTube channel, filled with educational content, is just a click away. In addition, we provide direct links to YouTube videos and insightful blogs that cater to the diverse needs of students, ensuring that they have the resources they need to excel in their studies.",
                imageResId = R.drawable.child_takingvirtual_courses,
                backgroundColor = "#F6FFD2"
            ),
            ServiceItem(
                title = "Charting Your Path: Free Career Guidance",
                description = "The journey to a successful career can be daunting, especially for students navigating the transition from school to work. Learn and Achieve offers free career guidance services to help students make informed choices about their future. Our experts provide invaluable insights and advice, empowering students to make confident decisions about their career paths, ultimately leading them toward a brighter, more fulfilling future.",
                imageResId = R.drawable.free_career_guidance,
                backgroundColor = "#FFE8E8"
            ),
            ServiceItem(
                title = "Supporting Every Learner: Special Education and Remedial Services",
                description = "We believe in an inclusive education system, and that's why Learn and Achieve offers free special education and remedial services. For children who require additional support, our psychiatric services are designed to address their unique needs. Our team of professionals is dedicated to helping every child reach their full potential, ensuring that no student is left behind.",
                imageResId = R.drawable.child_takingvirtual_courses,
                backgroundColor = "#FFE4B5"
            ),
            ServiceItem(
                title = "Charting Your Path: Free Career Guidance",
                description = "The journey to a successful career can be daunting, especially for students navigating the transition from school to work. Learn and Achieve offers free career guidance services to help students make informed choices about their future. Our experts provide invaluable insights and advice, empowering students to make confident decisions about their career paths, ultimately leading them toward a brighter, more fulfilling future.",
                imageResId = R.drawable.free_career_guidance,
                backgroundColor = "#FFE8E8"
            ),
            ServiceItem(
                title = "Charting Your Path: Free Career Guidance",
                description = "The journey to a successful career can be daunting, especially for students navigating the transition from school to work. Learn and Achieve offers free career guidance services to help students make informed choices about their future. Our experts provide invaluable insights and advice, empowering students to make confident decisions about their career paths, ultimately leading them toward a brighter, more fulfilling future.",
                imageResId = R.drawable.free_career_guidance,
                backgroundColor = "#FFE8E8"
            )
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOurServiceBinding.inflate(inflater, container, false)
        return binding.root
    }


    // onViewCreated is called after the view is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    // Initialize RecyclerView with a LinearLayoutManager
    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = OurServiceAdapter(services)
        binding.recyclerView.adapter = adapter
    }



    // Clean up binding reference when the view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
