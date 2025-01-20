package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.databinding.FragmentBlogDetailsBinding
import com.bumptech.glide.Glide

class BlogDetailsFragment : Fragment() {

    private var _binding: FragmentBlogDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogDetailsBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val title = arguments?.getString("title") ?: "No Title"
        val date = arguments?.getString("date") ?: "No Date"
        val description = arguments?.getString("description") ?: "No Description Available"
        val imageUrl = arguments?.getString("imageUrl") ?: ""

        binding.tvTitle.text = title
        binding.tvDate.text = date
        binding.tvDescription.text = description

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
