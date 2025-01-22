package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.ItemPurchasePackageBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.main_package.ui.fragment.PurchasePackageFragment
import com.anurupjaiswal.learnandachieve.model.CartResponse
import com.anurupjaiswal.learnandachieve.model.PackageData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PurchasePackageAdapter(
    private val context: Context,
    private val packages: List<PackageData>,
    private val token: String,
    private val onPackageDetailsClick: (String,String) -> Unit // Add a callback for the details click

) : RecyclerView.Adapter<PurchasePackageAdapter.PackageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPurchasePackageBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return PackageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val packageData = packages[position]
        holder.bind(packageData)
    }

    override fun getItemCount(): Int {
        return packages.size
    }

    inner class PackageViewHolder(private val binding: ItemPurchasePackageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(packageData: PackageData) {
            // Set title
            binding.title.text = packageData.packageName

            // Load image using Glide or Picasso
            Utils.Picasso(packageData.mainImage, binding.image, R.drawable.ic_package)

            // Set price
            binding.price.text = "₹${packageData.discountedPrice}"
            binding.strikethroughPrice.text = "₹${packageData.actualPrice}"
            binding.strikethroughPrice.paintFlags =
                binding.strikethroughPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // Handle Add to Cart button click
            binding.mcvAddToCart.setOnClickListener {
                E("Package ID: ${packageData.package_id}")
              addToCart(packageData.package_id,binding)
            }


            binding.tvPackageDetails.setOnClickListener {


                onPackageDetailsClick(packageData.package_id,token)
            }
        }
    }

    private fun addToCart(packageId: String,binding: ItemPurchasePackageBinding) {
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
                        if (cartResponse != null) {
                            cartResponse.cartData?.let { updateCartCount(it.cartCount) }
                        }
                    } else {
                        handleApiError(response,binding)
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
    private fun handleApiError(response: Response<CartResponse>,binding: ItemPurchasePackageBinding) {
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
                    Utils.UnAuthorizationToken(context)
                }
            }
            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->
                    Utils.toggleProgressBarAndText(false, binding.loading, binding.tvAddTOCard,binding.root)

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

    private fun updateCartCount(cartCount:Int) {
        if (context is DashboardActivity) {
            context.updateCartCount(cartCount) // Update the cart count in the activity
        }
    }

}
