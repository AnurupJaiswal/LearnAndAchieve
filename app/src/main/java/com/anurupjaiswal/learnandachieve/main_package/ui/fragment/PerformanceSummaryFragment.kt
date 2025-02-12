package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentPerformanceSummaryBinding
import com.anurupjaiswal.learnandachieve.model.PerformanceData
import com.anurupjaiswal.learnandachieve.model.PerformanceSummaryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerformanceSummaryFragment : Fragment() {

    private var _binding: FragmentPerformanceSummaryBinding? = null
    private val binding get() = _binding!!

    // Retrieve arguments passed from the previous fragment
    private var mockTestId: String? = null
    private var mockTestSubmissionsId: String? = null
    private var packageId: String? = null
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            mockTestId = bundle.getString("mockTest_id")
            mockTestSubmissionsId = bundle.getString("mockTestSubmissions_id")
            packageId = bundle.getString("package_id")
            orderId = bundle.getString("order_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerformanceSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Validate required parameters before proceeding
        if (mockTestId.isNullOrEmpty() || mockTestSubmissionsId.isNullOrEmpty()) {
          E("Invalid test parameters")
            return
        }

        // Fetch the performance summary data
        fetchPerformanceSummary()

        // Navigate to ResultQuestionsAndAnswersFragment on tap of the TextView.
        binding.tvViewQuestionsAns.setOnClickListener {
            val bundle = Bundle().apply {
                putString("mockTest_id", mockTestId)
                putString("package_id", packageId)
                putString("order_id", orderId)
                putString("mockTestSubmissions_id", mockTestSubmissionsId)

            }
            findNavController().navigate(R.id.ResultQuestionsAndAnswersFragment, bundle)
        }
    }

    private fun fetchPerformanceSummary() {
      //  binding.progressBar.visibility = View.VISIBLE

        val token = Utils.GetSession().token
        RetrofitClient.client.getPerformanceSummary(
            "Bearer $token",
            mockTestId!!,
            mockTestSubmissionsId!!
        ).enqueue(object : Callback<PerformanceSummaryResponse> {
            override fun onResponse(
                call: Call<PerformanceSummaryResponse>,
                response: Response<PerformanceSummaryResponse>
            ) {
              //  binding.progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val performanceData = response.body()!!.data
                    updateUI(performanceData)
                } else {
                    Log.e(
                        "PerformanceSummary",
                        "API Error: Code ${response.code()} - ${response.message()}"
                    )
                 E("Error fetching performance data")

                }
            }

            override fun onFailure(call: Call<PerformanceSummaryResponse>, t: Throwable) {
             //   binding.progressBar.visibility = View.GONE
                E("Network Error: ${t.localizedMessage}")

            }
        })
    }

    private fun updateUI(data: PerformanceData) {
        binding.apply {
            // Update UI elements with the API data.
            tvMarks.text = "${data.score} / ${data.totalMarks}"
            tvTimeTaken.text = data.submittedTime
            tvAttempted.text = "${data.totalAttemptQuestions} / ${data.totalQuestions}"
            tvCorrect.text = "${data.totalCorrectQuestions} / ${data.totalQuestions}"
            tvIncorrect.text = "${data.totalIncorrectQuestions} / ${data.totalQuestions}"
            // Optionally update the mock test name if provided in the data.
            tvMockTestName.text = data.mockTestName
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
