package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentFAQBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.FAQCategoryAdapter
import com.anurupjaiswal.learnandachieve.model.FAQCategory
import com.anurupjaiswal.learnandachieve.model.FAQResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FAQFragment : Fragment() {

    private lateinit var faqCategoryAdapter: FAQCategoryAdapter
    private lateinit var apiService: ApiService
    private lateinit var categories: List<FAQCategory>

    // Declare binding object
    private var _binding: FragmentFAQBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize view binding
        _binding = FragmentFAQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the ApiService
        apiService = RetrofitClient.client

        // Set up the adapter
        faqCategoryAdapter = FAQCategoryAdapter(emptyList()) { categoryId, position ->
            // Handle category click to fetch FAQs
            fetchFAQsForCategory(categoryId, position)
        }

        // Set up RecyclerView with the adapter
        binding.recyclerViewCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCategories.adapter = faqCategoryAdapter

        // Fetch categories when the fragment is created
        fetchCategories()
    }

    private fun fetchCategories() {
        apiService.getCategories(20, 0).enqueue(object : Callback<FAQResponse> {
            override fun onResponse(call: Call<FAQResponse>, response: Response<FAQResponse>) {
                if (response.isSuccessful && response.body()?.message == "Success") {
                    // Get the category data from the response
                    categories = response.body()?.data?.faqCategoryData ?: emptyList()
                    faqCategoryAdapter.updateCategories(categories)
                } else {
                  E( "Failed to fetch categories")
                }
            }

            override fun onFailure(call: Call<FAQResponse>, t: Throwable) {
                E("Error: ${t.message}")
            }
        })
    }

    private fun fetchFAQsForCategory(categoryId: String, position: Int) {
        apiService.getFAQsByCategory(10, 0, categoryId).enqueue(object : Callback<FAQResponse> {
            override fun onResponse(call: Call<FAQResponse>, response: Response<FAQResponse>) {
                if (response.isSuccessful && response.body()?.message == "Success") {
                    // Get the questions for the selected category
                    val questions = response.body()?.data?.faqsData ?: emptyList()

                    // Update the category's questions
                    categories[position].questions = questions

                    // Notify the adapter that the questions have been updated for this category
                    faqCategoryAdapter.notifyItemChanged(position)
                } else {
                    E( "Error: Failed to fetch FAQs")
                }
            }

            override fun onFailure(call: Call<FAQResponse>, t: Throwable) {
               E( "Error: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid memory leaks by setting the binding object to null
        _binding = null
    }
}
