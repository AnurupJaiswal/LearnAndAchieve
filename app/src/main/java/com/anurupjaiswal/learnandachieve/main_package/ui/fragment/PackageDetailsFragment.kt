package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.anurupjaiswal.learnandachieve.R


import com.anurupjaiswal.learnandachieve.databinding.FragmentPackageDetailsBinding

class PackageDetailsFragment : Fragment(R.layout.fragment_package_details) {

    private var _binding: FragmentPackageDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPackageDetailsBinding.bind(view)

        // Retrieve arguments from the bundle
        val title = arguments?.getString("title")
        val price = arguments?.getString("price")
        val originalPrice = arguments?.getString("originalPrice")
        val imageRes = arguments?.getInt("imageRes")
        val description = arguments?.getString("description")

        // Bind the data to the views
        binding.titleTextView.text = title ?: "No Title"
        binding.price.text = "₹ ${price ?: "0"}"
        binding.strikethroughPrice.text = "₹ ${originalPrice ?: "0"}"
        binding.descriptionTextView.text = description ?: "No Description"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
