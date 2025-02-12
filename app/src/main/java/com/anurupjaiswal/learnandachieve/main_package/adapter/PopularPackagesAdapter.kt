package com.anurupjaiswal.learnandachieve.main_package.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Constants
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.ItemPackageCardBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.CartResponse
import com.anurupjaiswal.learnandachieve.model.PackageData
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PopularPackagesAdapter(
    private val context: Context,
    private val packages: List<PackageData>,
    private val token: String,
    private val onPackageDetailsClick: (String,String) -> Unit // Add a callback for the details click

) : RecyclerView.Adapter<PopularPackagesAdapter.PackageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val binding = ItemPackageCardBinding.inflate(
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

    inner class PackageViewHolder(private val binding: ItemPackageCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(packageData: PackageData) {
            binding.tvBadge.visibility = if (position == 0) View.VISIBLE else View.INVISIBLE

            binding.tvTitle.text = packageData.packageName

            // Load image using Glide or Picasso
            Utils.Picasso(packageData.mainImage, binding.imagePackage, R.drawable.ic_package)

            // Set price
            binding.discountedPrice.text = "₹${packageData.discountedPrice}"
            binding.originalPrice.text = "₹${packageData.actualPrice}"
            binding.originalPrice.paintFlags =
                binding.originalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            // Handle Add to Cart button click
            binding.buyNowText.setOnClickListener {
                E("Package ID: ${packageData.package_id}")
                addToCart(packageData.package_id,binding)
            }


            binding.root.setOnClickListener {


                onPackageDetailsClick(packageData.package_id,token)
            }
        }
    }

    private fun addToCart(packageId: String,binding: ItemPackageCardBinding) {
        val apiService = RetrofitClient.client


        val requestBody = mapOf(Constants.PackageId to packageId)

        apiService.addToCart(token, requestBody).enqueue(object : Callback<CartResponse> {
            override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
                // Hide loading state

                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val cartResponse = response.body()
                        
                        Utils.T(context, "Add To Cart Successfully")
                        if (cartResponse != null) {
                            cartResponse.cartData?.let { updateCartCount(it.cartCount) }
                        }
                    } else {
                        handleApiError(response,binding)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    E("Error processing the request.")
                }
            }

            override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                t.printStackTrace()
                E(t.message ?: "Request failed. Please try again later.")
            }
        })
    }
    private fun handleApiError(response: Response<CartResponse>, binding: ItemPackageCardBinding) {
        when (response.code()) {
            StatusCodeConstant.BAD_REQUEST -> {

                response.errorBody()?.let { errorBody ->
                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.error ?: "Invalid Request"
                    E(displayMessage)

                    //      Utils.T(context, displayMessage)
                }
            }
            StatusCodeConstant.UNAUTHORIZED -> {
                response.errorBody()?.let { errorBody ->

                    val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                    val displayMessage = message.message ?: "Unauthorized Access"
               //     Utils.T(context, displayMessage)
                    E(displayMessage)
                    Utils.UnAuthorizationToken(context)
                }
            }
            StatusCodeConstant.NOT_FOUND -> {
                response.errorBody()?.let { errorBody ->

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