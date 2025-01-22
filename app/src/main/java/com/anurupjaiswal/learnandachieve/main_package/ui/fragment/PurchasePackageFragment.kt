package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.database.UserDataHelper
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.SavedData
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentPurchasePackageBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.PurchasePackageAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.PackageResponse
import com.anurupjaiswal.learnandachieve.model.PackageModel
import retrofit2.Call
import retrofit2.Response

class PurchasePackageFragment : Fragment() {

    private var _binding: FragmentPurchasePackageBinding? = null
    private val binding get() = _binding!!
    private var apiservice: ApiService? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasePackageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiservice = RetrofitClient.client
        init()
    }


    fun init() {
        apiservice = RetrofitClient.client
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        getPackages()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getPackages() {

        val token = Utils.GetSession().token
        val userId = Utils.GetSession()._id
        E("token $token")
        E("userId $userId")
        val authToken = "Bearer $token"

        apiservice?.getPackages(authToken, limit = 10, offset = 0)
            ?.enqueue(object : retrofit2.Callback<PackageResponse> {
                override fun onResponse(
                    call: Call<PackageResponse>,
                    response: Response<PackageResponse>
                ) {
                    try {
                        if (response.code() == StatusCodeConstant.OK) {
                            val packageResponse = response.body()
                            if (packageResponse != null) {


                                val packageList = packageResponse.packages
                                val adapter =
                                    PurchasePackageAdapter(requireContext(), packageList, authToken,
                                        onPackageDetailsClick = { packageId, token ->
                                            navigateToPackageDetails(packageId, token)
                                        }
                                    )
                                binding.recyclerView.adapter = adapter
                                adapter.notifyDataSetChanged()  // Notify that the data has been updated

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<PackageResponse>, t: Throwable) {
                    call.cancel()
                    t.printStackTrace()
                    Utils.T(activity, t.message)
                    E("getMessage::" + t.message)
                }
            })
    }

    fun navigateToPackageDetails(packageId: String, token: String) {
        val bundle = Bundle().apply {
            putString("packageId", packageId)
            putString("token", token)
        }

        // Navigate to PackageDetailsFragment with the Bundle
        NavigationManager.navigateToFragment(findNavController(), R.id.packageDetailsFragment, bundle)

    }
}

