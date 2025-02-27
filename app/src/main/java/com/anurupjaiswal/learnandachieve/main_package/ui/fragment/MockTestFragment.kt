package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.floatIntMapOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentMockTestBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.MockTestAdapter

import com.anurupjaiswal.learnandachieve.model.MockTestItem
import com.anurupjaiswal.learnandachieve.model.MockTestResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MockTestFragment : Fragment() {

    private var _binding: FragmentMockTestBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MockTestAdapter
    private val mockTestList = mutableListOf<MockTestItem>()
    private var mockTestCall: Call<MockTestResponse>? = null
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
        fetchMockTests()
        binding.mcvGotoPackages.setOnClickListener {
            NavigationManager.navigateToFragment(
                findNavController(), R.id.PurchasePackage
            )
        }
    }

    private fun showProgressBar(isVisible: Boolean) {
        _binding?.let { binding ->
            binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
            binding.rvMockTest.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }
    private fun setupRecyclerView() {
        adapter = MockTestAdapter(
            mockTestList,
            onMockTestSelected = { mockTest ->
                val bundle = Bundle().apply {
                    putString("mockTest_id", mockTest.mockTest_id)
                    putString("package_id", mockTest.package_id)
                    putString("order_id", mockTest.order_id)
                }
                NavigationManager.navigateToFragment(findNavController(), R.id.QuestionComparisonFragment, bundle)
            },
            onViewResultsSelected = { mockTest ->
                val bundle = Bundle().apply {
                    putString("mockTest_id", mockTest.mockTest_id)
                    putString("package_id", mockTest.package_id)
                    putString("order_id", mockTest.order_id)
                }
                NavigationManager.navigateToFragment(findNavController(), R.id.ShowResultFragment, bundle)
            }
        )

        binding.rvMockTest.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MockTestFragment.adapter
        }
    }


    private fun fetchMockTests() {
        showProgressBar(true)

        val authToken = "Bearer ${Utils.GetSession().token}"

        RetrofitClient.client.getMockTests(authToken, limit = 20, offset = 0)
            .enqueue(object : Callback<MockTestResponse> {
                override fun onResponse(call: Call<MockTestResponse>, response: Response<MockTestResponse>) {
                    showProgressBar(    false)

                    when (response.code()) {

                        StatusCodeConstant.OK -> {
                            showProgressBar(false)
                            response.body()?.let { mockTestResponse ->
                                if (mockTestResponse.data.isNotEmpty()){
                                    _binding?.let { binding ->
                                        binding.llEmptyLayout.visibility = View.GONE
                                        binding.rvMockTest.visibility = View.VISIBLE
                                        mockTestList.clear()
                                        mockTestList.addAll(mockTestResponse.data)
                                        adapter.notifyDataSetChanged()
                                    }

                                }else{
                                    binding.rvMockTest.visibility = View.GONE
                                    binding.llEmptyLayout.visibility = View.VISIBLE
                                }

                            }
                        }

                        StatusCodeConstant.UNAUTHORIZED -> {
                            showProgressBar(false)

                            E("fetchMockTests UNAUTHORIZED: ${response.message()}")
                            Utils.UnAuthorizationToken(requireContext())
                        }

                        StatusCodeConstant.BAD_REQUEST -> {
                            showProgressBar(false)

                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                E("fetchMockTests BAD_REQUEST: $errorMessage")
                            }
                        }

                        else -> {
                            E("fetchMockTests Error: ${response.code()} - ${response.errorBody()?.string()}")
                        }
                    }
                }

                override fun onFailure(call: Call<MockTestResponse>, t: Throwable) {
                    showProgressBar(false)
                    E("fetchMockTests Failure: ${t.message}")
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}
