package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils


import com.anurupjaiswal.learnandachieve.databinding.FragmentPackageDetailsBinding
import com.anurupjaiswal.learnandachieve.databinding.ItemPurchasePackageBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.CartResponse
import com.anurupjaiswal.learnandachieve.model.PackageDetailsResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class PackageDetailsFragment : Fragment(R.layout.fragment_package_details) {

    private var _binding: FragmentPackageDetailsBinding? = null
    private val binding get() = _binding!!

    private var authToken: String? = null
    private var packageId: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPackageDetailsBinding.bind(view)

        // Retrieve the arguments safely in onViewCreated
        packageId = arguments?.getString("packageId")
        authToken = arguments?.getString("token")
        getPackageDetails(authToken, packageId)


        binding.mcvAddToCart.setOnClickListener {

            addToCart(authToken,packageId)
        }

    }


    private fun getPackageDetails(authToken: String?, packageId: String?) {
        val apiService = RetrofitClient.client

        apiService.getPackageDetails(authToken, packageId).enqueue(object : Callback<PackageDetailsResponse> {
            override fun onResponse(call: Call<PackageDetailsResponse>, response: Response<PackageDetailsResponse>) {
                // Hide loading state
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val packageDetailsResponse = response.body()
                        packageDetailsResponse?.packageDetails?.let { details ->
                            binding.titleTextView.text = details.packageName
                            binding.price.text = "₹ ${details.actualPrice}"
                            binding.strikethroughPrice.text = "₹ ${details.discountedPrice}"
                             Utils.SetHtmlContent(binding.descriptionTextView,details.details)

                        }
                        } else {
                        handlePackageDetailsApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(context, "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<PackageDetailsResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                t.printStackTrace()
                Utils.T(context, t.message ?: "Request failed. Please try again later.")
            }
        })
    }


    private fun addToCart(token:String?,packageId: String?) {
        val apiService = RetrofitClient.client
        Utils.toggleProgressBarAndText(true, binding.loading, binding.tvAddTOCard, binding.root)


        val requestBody = mapOf(Constants.PackageId to packageId)

        apiService.addToCart(token, requestBody).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                // Hide loading state

                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val cartResponse = response.body()
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvAddTOCard, binding.root)

                        Utils.T(context, "Add To Cart Successfully")
                        updateCartCount(cartResponse!!.cartData!!.cartCount)
                    } else {
                        handleCartApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(context, "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvAddTOCard, binding.root)
                t.printStackTrace()
                Utils.T(context, t.message ?: "Request failed. Please try again later.")
            }
        })
    }

    private fun handlePackageDetailsApiError(response: Response<PackageDetailsResponse>) {
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.error ?: "Invalid Request"
                    Utils.T(requireContext(), displayMessage)
                }
            }
            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Unauthorized Access"
                    Utils.T(context, displayMessage)
                    Utils.UnAuthorizationToken(requireContext())
                }
            }
            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Package not found. Please try again."
                    Utils.T(requireContext(), displayMessage)
                }
            }
            else -> {
                Utils.T(context, "Unknown error occurred.")
            }
        }
    }
    private fun handleCartApiError(response: Response<CartResponse>) {
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> {
                Utils.toggleProgressBarAndText(false, binding.loading, binding.tvAddTOCard, binding.root)

                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.error ?: "Invalid Request"
                    Utils.T(context, displayMessage)
                }
            }
            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->
                    Utils.toggleProgressBarAndText(false, binding.loading, binding.tvAddTOCard, binding.root)

                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Unauthorized Access"
                    Utils.T(context, displayMessage)
                    Utils.UnAuthorizationToken(requireContext())
                }
            }
            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->
                    Utils.toggleProgressBarAndText(false, binding.loading, binding.tvAddTOCard, binding.root)

                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Package not found. Please try again."
                    Utils.T(context, displayMessage)
                }
            }
            else -> {
                Utils.T(context, "Unknown error occurred.")
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCartCount(cartCount:Int) {
        (context as? DashboardActivity)?.updateCartCount(cartCount)
    }

}
