package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentMockTestBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.MockTestAdapter

import com.anurupjaiswal.learnandachieve.model.MockTestItem
import com.anurupjaiswal.learnandachieve.model.MockTestResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MockTestFragment : Fragment() {

    private var _binding: FragmentMockTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MockTestAdapter
    private val mockTestList = mutableListOf<MockTestItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMockTestBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchMockTests()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = MockTestAdapter(mockTestList)
        binding.rvMockTest.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MockTestFragment.adapter
        }
    }

    private fun fetchMockTests() {

        val authToken = "Bearer ${Utils.GetSession().token}"  // Assuming the token is stored in session

        RetrofitClient.client.getMockTests(authToken,limit = 20, offset = 0)
            .enqueue(object : Callback<MockTestResponse> {
                override fun onResponse(call: Call<MockTestResponse>, response: Response<MockTestResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        mockTestList.clear()
                        mockTestList.addAll(response.body()!!.data)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MockTestResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
