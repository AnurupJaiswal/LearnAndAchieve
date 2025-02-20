package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatExamTicketBinding
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BharatSatExamTicketFragment : Fragment() {

    private var _binding: FragmentBharatSatExamTicketBinding? = null
    private val binding get() = _binding!! // Safe access to binding
    private var apiservice: ApiService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBharatSatExamTicketBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.GetSession().token?.let { getUserDetails(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getUserDetails(authToken: String) {

        apiservice?.getUserDetails(authToken)?.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val userModel = response.body()

                        if (userModel?.user != null) {
                            val getUser = userModel.user

                            E("User ID: ${getUser.user_id}")
                            E("Full API Response: ${response.body()}")



                        } else {
                            // Log or handle case where 'getUser' is null
                            E("Error: 'getUser' is null in response.")
                        }

                    } else {
                        handleGetUserResponseApiError(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Utils.T(requireContext(), "Error processing the request.")
                }
            }

            override fun onFailure(call: Call<GetUserResponse>, t: Throwable) {
                call.cancel()
                // Hide loading state
                t.printStackTrace()
                Utils.T(requireContext(), t.message ?: "Request failed. Please try again later.")
            }
        })


    }
    private fun handleGetUserResponseApiError(response: Response<GetUserResponse>) {
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
                    Utils.T(requireContext(), displayMessage)
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
                Utils.T(requireContext(), "Unknown error occurred.")

            }
        }
    }
}
