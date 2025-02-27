package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R


import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentShowResultBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.ShowResultAdapter
import com.anurupjaiswal.learnandachieve.model.ShowResult
import com.anurupjaiswal.learnandachieve.model.ShowResultData
import com.anurupjaiswal.learnandachieve.model.ShowResultReponce
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowResultFragment : Fragment() {

    private var _binding: FragmentShowResultBinding? = null
    private val binding get() = _binding!!
    private var mockTestId: String? = null
    private var package_id: String? = null
    private var order_id: String? = null

    // List to hold API data
    private var showResultDataList: List<ShowResultData> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mockTestId = arguments?.getString("mockTest_id")
        package_id = arguments?.getString("package_id")
        order_id = arguments?.getString("order_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowResultBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchResults()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter =
            ShowResultAdapter(showResultDataList) { mockTestSubmissionsId, mockTestId ->
                val bundle = Bundle().apply {
                    putString("mockTestSubmissions_id", mockTestSubmissionsId)
                    putString("mockTest_id", mockTestId)
                }
                NavigationManager.navigateToFragment(
                    findNavController(),
                    R.id.PerformanceSummaryFragment,
                    bundle
                )
            }
    }

    // Update the adapter with new data from the API
    private fun updateRecyclerView(newData: List<ShowResultData>) {
        showResultDataList = newData
        binding.rvResults.adapter =
            ShowResultAdapter(showResultDataList) { mockTestSubmissionsId, mockTestId ->
                val bundle = Bundle().apply {
                    putString("mockTestSubmissions_id", mockTestSubmissionsId)
                    putString("mockTest_id", mockTestId)
                }
                NavigationManager.navigateToFragment(
                    findNavController(),
                    R.id.PerformanceSummaryFragment,
                    bundle
                )
            }
    }


    private fun fetchResults() {
        // Replace these with your actual token and IDs
        val token = Utils.GetSession().token


        // Make the API call with enqueue (callback style)
        RetrofitClient.client.getShowResults("Bearer $token", mockTestId!!, order_id!!)
            .enqueue(object : Callback<ShowResultReponce> {
                override fun onResponse(
                    call: Call<ShowResultReponce>,
                    response: Response<ShowResultReponce>
                ) {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            response.body()?.let { apiResponse ->
                                if (apiResponse.data.isEmpty()) {
                                    binding.llEmptyLayout.visibility = View.VISIBLE
                                } else {
                                    binding.llEmptyLayout.visibility = View.GONE

                                    updateRecyclerView(apiResponse.data)
                                }
                            }
                        }

                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.E("getShowResults UNAUTHORIZED: ${response.message()}")
                            Utils.UnAuthorizationToken(requireContext())
                        }

                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError =
                                    Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                Utils.E("getShowResults BAD_REQUEST: $errorMessage")
                                Utils.T(requireContext(), errorMessage) // Show user-friendly error
                            }
                        }

                        else -> {
                            Utils.E(
                                "getShowResults Error: ${response.code()} - ${
                                    response.errorBody()?.string()
                                }"
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<ShowResultReponce>, t: Throwable) {
                    Utils.E("getShowResults Failure: ${t.message}")
                }
            })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}