package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentDeleteAccountBinding
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DeleteuserverifactionActivity
import com.anurupjaiswal.learnandachieve.model.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DeleteAccountFragment : Fragment() {

     private lateinit var binding : FragmentDeleteAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

         binding = FragmentDeleteAccountBinding.inflate(inflater,container,false)


         binding.lbDeleteAccount.setOnClickListener {
             Utils.toggleProgressBarAndText(true, binding.loading, binding.tvOtpVerification,binding.root)

             deleteUser()


         }

         return binding.root
    }
    private fun deleteUser() {


        val token = "Bearer ${Utils.GetSession().token}"

        // Make the API call to delete the user
        val apiService = RetrofitClient.client
        apiService.deleteUser(token).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                when (response.code()) {
                    StatusCodeConstant.OK -> {
                        // Successfully sent OTP
                        val message = response.body()?.message ?: "OTP Sent Successfully"
                        Utils.T(requireContext(), message)
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

Utils.I(requireContext(),DeleteuserverifactionActivity::class.java,null)
                    }
                    StatusCodeConstant.NOT_FOUND -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "User not found"
                        Utils.T(requireContext(), message)
                    }
                    StatusCodeConstant.UNAUTHORIZED -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "Unauthorized access"
                        Utils.T(requireContext(), message)
                    }
                    StatusCodeConstant.BAD_REQUEST -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "Bad request"
                        Utils.T(requireContext(), message)
                    }
                    else -> {
                        Utils.toggleProgressBarAndText(false, binding.loading, binding.tvOtpVerification,binding.root)

                        val message = response.body()?.message ?: "An error occurred"
                        Utils.T(requireContext(), message)
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                // Handle request failure
                Utils.T(requireContext(), "Request failed. Please try again.")
            }
        })
    }

}