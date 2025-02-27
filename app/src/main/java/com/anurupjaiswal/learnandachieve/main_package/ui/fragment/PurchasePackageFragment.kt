package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentPurchasePackageBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.PurchasePackageAdapter
import com.anurupjaiswal.learnandachieve.model.PackageResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback


class PurchasePackageFragment : Fragment() {

    private var _binding: FragmentPurchasePackageBinding? = null
    private val binding get() = _binding!!
    private var apiService: ApiService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasePackageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        init()
    }



    private fun init() {
        apiService = RetrofitClient.client
        binding.rcvPurchasePackage.layoutManager = LinearLayoutManager(context)
        fetchPackages()


        // Listen for changes in network status
    }

    private fun fetchPackages() {
        showProgressBar(true)
        val authToken = "Bearer ${Utils.GetSession().token}"

        apiService?.getPackages(authToken, limit = 10, offset = 0)
            ?.enqueue(object : Callback<PackageResponse> {
                override fun onResponse(call: Call<PackageResponse>, response: Response<PackageResponse>) {
                    showProgressBar(false)
                    if (_binding == null) return
                    when (response.code()) {
                        StatusCodeConstant.OK -> response.body()?.packages?.let { packageList ->
                            if (packageList.isNotEmpty()) {
                                binding.noInternetText.visibility = View.GONE
                                binding.rcvPurchasePackage.adapter = PurchasePackageAdapter(
                                    requireContext(), packageList, authToken,
                                    onPackageDetailsClick = { packageId, token ->
                                        val bundle = Bundle().apply {
                                            putString("packageId", packageId)
                                            putString("token", token)
                                        }
                                        NavigationManager.navigateToFragment(
                                            findNavController(),
                                            R.id.packageDetailsFragment,
                                            bundle
                                        )
                                    }
                                )
                            } else binding.noInternetText.visibility = View.VISIBLE
                             binding.noInternetText.text = "Not Available"
                        }
                        StatusCodeConstant.UNAUTHORIZED -> Utils.UnAuthorizationToken(requireContext())
                        else ->

                            showProgressBar(false)

                    }
                }

                override fun onFailure(call: Call<PackageResponse>, t: Throwable) {
                    E(t.message!!)
                    showProgressBar(false)
                }
            })
    }

    private fun showProgressBar(isVisible: Boolean) {
        binding.progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.rcvPurchasePackage.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}


