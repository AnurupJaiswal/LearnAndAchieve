package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.databinding.FragmentTermsAndConditionsBinding
import com.anurupjaiswal.learnandachieve.model.TermsConditionsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TermsAndConditionsFragment : Fragment() {

    private lateinit var binding: FragmentTermsAndConditionsBinding
    private val apiService = RetrofitClient.client

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.VISIBLE



        val isFromAccountFragment = arguments?.getBoolean("isFromAccountFragment", false) ?: false

        if (isFromAccountFragment) {
            fetchTermsAndConditions()  // Fetch terms for account fragment
        } else {
            //fetchRegistrationTerms()  // Fetch terms for registration fragment
        }

        fetchTermsAndConditions()
        return binding.root
    }

    private fun fetchTermsAndConditions() {
        apiService.getTermsConditions().enqueue(object : Callback<TermsConditionsResponse> {
            override fun onResponse(
                call: Call<TermsConditionsResponse>,
                response: Response<TermsConditionsResponse>
            ) {
                if (response.isSuccessful) {

                        response.body()?.data?.details?.let { htmlContent ->
                            displayTermsAndConditions(htmlContent)
                        }





                } else {
                    Toast.makeText(requireContext(), "Failed to load terms", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TermsConditionsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayTermsAndConditions(htmlContent: String) {
        val customCss = """
        <style>
            body {
                font-family: 'Gilroy-Semibold', sans-serif;
                font-size: 14px;
                line-height: 1.3;
            }
            
               * {
            -webkit-user-select: none; /* Safari */
            -moz-user-select: none; /* Firefox */
            -ms-user-select: none; /* Internet Explorer/Edge */
            user-select: none; /* Standard */
        }
        </style>
    """

        // Inject the custom CSS before the HTML content

        val modifiedHtmlContent = customCss + htmlContent
        binding.webViewTerms.apply {
            // Disable scrollbars
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false

            // Enable JavaScript if required
            settings.javaScriptEnabled = true
        }
        binding.webViewTerms.loadDataWithBaseURL(
            null,
            modifiedHtmlContent,
            "text/html",
            "UTF-8",
            null
        )

        // Set WebView client to listen for loading events
        binding.webViewTerms.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                binding.progressBar.visibility = View.GONE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.visibility = View.VISIBLE
            }
        }
    }

}
