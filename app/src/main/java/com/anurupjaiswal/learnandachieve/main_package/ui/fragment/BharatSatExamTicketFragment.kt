package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.GetSession
import com.anurupjaiswal.learnandachieve.databinding.FragmentBharatSatExamTicketBinding
import com.anurupjaiswal.learnandachieve.model.GetUserResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BharatSatExamTicketFragment : Fragment() {

    private var countDownTimer: CountDownTimer? = null
    private var _binding: FragmentBharatSatExamTicketBinding? = null
    private val binding get() = _binding!!

    private var apiService: ApiService? = null
    private var bharatSatExamId: String? = null
    private var eHallTicketId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBharatSatExamTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        apiService = RetrofitClient.client

        val token = "Bearer ${Utils.GetSession().token}"
        getUserDetails(token)

        binding.rlBharatSatExam.setOnClickListener {
            if (!binding.rlBharatSatExam.isEnabled) {
                Utils.T(requireContext(), "The exam has not started yet. Please wait.")
            } else {
                val bundle = Bundle().apply {
                    putBoolean("isExamBharatSat", true)
                    putString("bharatSatExamId", bharatSatExamId)
                    putString("eHallTicketId", eHallTicketId)
                }
                NavigationManager.navigateToFragment(
                    findNavController(),
                    R.id.BharatSatExamInstructionsFragment,
                    bundle
                )
            }
        }

        binding.tvGuid.setOnClickListener {
            NavigationManager.navigateToFragment(
                findNavController(),
                R.id.BharatSatExamInstructionsFragment
            )
        }
    }





    private fun getUserDetails(authToken: String) {
        apiService?.getUserDetails(authToken)?.enqueue(object : Callback<GetUserResponse> {
            override fun onResponse(
                call: Call<GetUserResponse>,
                response: Response<GetUserResponse>
            ) {
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val serverDateStr = response.headers()["Date"]
                        var serverCurrentTime = System.currentTimeMillis() // fallback
                        if (serverDateStr != null) {
                            try {
                                // Parse the standard HTTP date format, e.g., "Tue, 15 Nov 1994 08:12:31 GMT"
                                val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).apply {
                                    timeZone = TimeZone.getTimeZone("GMT")
                                }
                                val serverDate = dateFormat.parse(serverDateStr)
                                serverCurrentTime = serverDate?.time ?: serverCurrentTime
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        val userModel = response.body()
                        if (userModel?.user != null) {
                            val user = userModel.user

                            if (user.examAttempted) {

                                binding.rlBharatSatExam.visibility = View.GONE
                                binding.tvBharatSatMsg.visibility = View.VISIBLE
                                binding.llGuidlines.visibility = View.GONE
                            } else {
                                binding.rlBharatSatExam.visibility = View.VISIBLE
                                binding.llGuidlines.visibility = View.VISIBLE
                                binding.tvBharatSatMsg.visibility = View.GONE


                            }

                            E("User ID: ${user.user_id}")
                            E("Full API Response: ${response.body()}")
                            startCountDownTimer(user.examStartTime, serverCurrentTime)
                            binding.tvName.text = "${user.firstName} ${user.lastName}"
                            binding.tvHallTicketNo.text = user.hallTicketNumber
                            binding.tvClass.text = user.class_name
                            binding.tvMedium.text = user.medium
                            binding.tvTime.text = Utils.formatExamTime(user.examStartTime, user.examEndTime)
                            binding.tvDate.text = Utils.formatExamDate(user.bharatSatExamDate)
                            bharatSatExamId = user.bharatSatExamId
                            eHallTicketId = user.eHallTicketId
                            binding.progressBar.visibility =View.GONE
                            binding.mcvTickit.visibility =View.VISIBLE

                        } else {
                            E("Error: User details are null in response.")
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
                t.printStackTrace()
                Utils.T(requireContext(), t.message ?: "Request failed. Please try again later.")
            }
        })
    }



    /**
     * Handle API error responses.
     */
    private fun handleGetUserResponseApiError(response: Response<GetUserResponse>) {
        binding.progressBar.visibility =View.GONE
        response.errorBody()?.let { errorBody ->
            val message = Gson().fromJson(errorBody.charStream(), APIError::class.java)
            val displayMessage = when (response.code()) {
                StatusCodeConstant.BAD_REQUEST -> message.error ?: "Invalid Request"
                StatusCodeConstant.UNAUTHORIZED -> {
                    Utils.UnAuthorizationToken(requireContext())
                    message.message ?: "Unauthorized Access"
                }
                StatusCodeConstant.NOT_FOUND -> message.message ?: "Package not found. Please try again."
                else -> "Unknown error occurred."
            }
            Utils.T(requireContext(), displayMessage)
        }
    }

    /**
     * Starts a countdown timer until the exam start time.
     *
     * @param examStartTimeStr The exam start time as a string in ISO 8601 format.
     */

    private fun startCountDownTimer(examStartTimeStr: String, referenceTime: Long) {
        // Show timer layout and disable the exam activation button
        binding.llTimer.visibility = View.VISIBLE
        binding.rlBharatSatExam.isEnabled = false
        binding.ivTabBg.setImageResource(R.drawable.bharat_sat_exam_incativetab)

        // Configure the date formatter for exam start time (assumed in UTC)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            val examStartDate: Date = sdf.parse(examStartTimeStr) ?: throw IllegalArgumentException("Invalid date")
            val examStartMillis = examStartDate.time
            // Use the server's reference time instead of System.currentTimeMillis()
            val timeLeftInMillis = examStartMillis - referenceTime

            if (timeLeftInMillis > 0) {
                countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val totalSeconds = millisUntilFinished / 1000
                        val days = totalSeconds / (24 * 3600)
                        val hours = (totalSeconds % (24 * 3600)) / 3600
                        val minutes = (totalSeconds % 3600) / 60
                        val seconds = totalSeconds % 60

                        binding.tvTimerDay.text = days.toString()
                        binding.tvTimerHours.text = hours.toString()
                        binding.tvTimerMin.text = minutes.toString()
                        binding.tvTimerSec.text = seconds.toString()
                    }

                    override fun onFinish() {
                        binding.llTimer.visibility = View.GONE
                        binding.rlBharatSatExam.isEnabled = true
                        binding.ivTabBg.setImageResource(R.drawable.bharat_sat_exam_activetab)
                        binding.tvTimerDay.text = "0"
                        binding.tvTimerHours.text = "0"
                        binding.tvTimerMin.text = "0"
                        binding.tvTimerSec.text = "0"
                    }
                }
                countDownTimer?.start()
            } else {
                binding.llTimer.visibility = View.GONE
                binding.tvTimerDay.text = "0"
                binding.tvTimerHours.text = "0"
                binding.tvTimerMin.text = "0"
                binding.tvTimerSec.text = "0"
                binding.rlBharatSatExam.isEnabled = true
                binding.ivTabBg.setImageResource(R.drawable.bharat_sat_exam_activetab)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Utils.T(requireContext(), "Error parsing exam start time.")
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }


}
