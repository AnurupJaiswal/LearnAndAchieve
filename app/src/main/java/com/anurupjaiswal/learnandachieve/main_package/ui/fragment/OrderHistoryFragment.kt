package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.FileDownloadListener
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentOrderHistoryBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.OrderHistoryAdapter
import com.anurupjaiswal.learnandachieve.model.Order
import com.anurupjaiswal.learnandachieve.model.OrderHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class OrderHistoryFragment : Fragment(), FileDownloadListener {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
 private  lateinit var apiService: ApiService
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        init()
    }

    fun init(){
        val token = "Bearer ${Utils.GetSession().token}"
        apiService = RetrofitClient.client

        fetchOrderHistory(token)

        binding.mcvGotoPackages.setOnClickListener {
            NavigationManager.navigateToFragment(
                findNavController(), R.id.PurchasePackage
            )
        }

        progressBar =binding.progressBar
        progressBar.progress = 0
        progressBar.visibility = View.GONE

    }

    private fun fetchOrderHistory(token: String) {

        apiService.getOrderHistory(token)
            .enqueue(object : Callback<OrderHistoryResponse> {
                override fun onResponse(
                    call: Call<OrderHistoryResponse>,
                    response: Response<OrderHistoryResponse>
                ) {
                    if (response.code() == StatusCodeConstant.OK) {
                        val orderHistory = response.body()
                        if (orderHistory != null) {
                            if (orderHistory.data.isEmpty()) {
                                // If data is empty, show empty layout and hide RecyclerView
                               binding.llEmptyLayout.visibility = View.VISIBLE
                                binding.rvOrderHistory.visibility = View.GONE
                            } else {
                                // If data is present, show RecyclerView and hide empty layout
                                binding.llEmptyLayout.visibility = View.GONE
                                binding.rvOrderHistory.visibility = View.VISIBLE
                                setupRecyclerView(orderHistory.data)
                            }
                        }
                    } else {
                        Utils.E("onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {

                    Utils.E("onFailure: ${t.message}")
                }
            })
    }

    private fun setupRecyclerView(orders: List<Order>) {
        val adapter = OrderHistoryAdapter(orders,this)
        binding.rvOrderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProgressUpdate(progress: Int) {
        // When download starts (progress == 0), disable touch events on the UI.
        if (progress == 0) {
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            progressBar.visibility = View.VISIBLE
        }
        progressBar.progress = progress
    }
    override fun onDownloadComplete(file: File) {
        progressBar.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onDownloadFailed(error: String) {
        progressBar.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }
}