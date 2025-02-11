package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NetworkUtil
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init()
    }


    private fun init() {
        setupRecyclerView()
        if (NetworkUtil.isInternetAvailable(requireContext())) {
            fetchMockTests()
        } else {
            showNoInternetMessage()
        }

        // Listen for changes in network status
        NetworkUtil.registerNetworkReceiver(requireContext()) { isConnected ->
            if (isConnected) {
                binding.noInternetText.visibility = View.GONE
                fetchMockTests() // Re-fetch data when internet is back
            } else {
                binding.rvMockTest.visibility = View.GONE
                binding.noInternetText.visibility = View.VISIBLE
            }
        }
    }
    private fun showNoInternetMessage() {
        binding.progressBar.visibility = View.GONE
        binding.rvMockTest.visibility = View.GONE
        binding.noInternetText.visibility = View.VISIBLE
    }

    private fun showProgressBar(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.rvMockTest.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    private fun setupRecyclerView() {
        adapter = MockTestAdapter(mockTestList) { mockTestId ->
            val bundle = Bundle().apply {

                putString("mockTest_id", mockTestId)
            }
           NavigationManager.navigateToFragment(findNavController(), R.id.QuestionComparisonFragment,bundle)
        }
        binding.rvMockTest.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MockTestFragment.adapter
        }
    }

    private fun fetchMockTests() {
        showProgressBar(true)

        val authToken = "Bearer ${Utils.GetSession().token}"  // Assuming the token is stored in session

        RetrofitClient.client.getMockTests(authToken,limit = 20, offset = 0)
            .enqueue(object : Callback<MockTestResponse> {
                override fun onResponse(call: Call<MockTestResponse>, response: Response<MockTestResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        mockTestList.clear()
                        mockTestList.addAll(response.body()!!.data)
                        adapter.notifyDataSetChanged()
                        showProgressBar(false)

                    } else {
                        E("Error: ${response.message()}")
                        E("Error: ${response.errorBody()}")
                        showNoInternetMessage()
                        showProgressBar(false)
                    }
                }

                override fun onFailure(call: Call<MockTestResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    showNoInternetMessage()
                    showProgressBar(false)

                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        NetworkUtil.unregisterNetworkCallback(requireContext())

    }
}
