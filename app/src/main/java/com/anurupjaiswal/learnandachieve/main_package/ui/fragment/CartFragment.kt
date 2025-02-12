package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.databinding.FragmentCartBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.CartItem
import com.anurupjaiswal.learnandachieve.model.CartSummary
import com.anurupjaiswal.learnandachieve.model.DeleteCartResponse
import com.google.gson.Gson

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartItems = mutableListOf<CartItem>()

    private lateinit var navController: NavController

    private var token: String? = null

    private lateinit var apiService: ApiService

    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        token = Utils.GetSession().token
        val dashboardActivity = requireActivity() as DashboardActivity
        navController = dashboardActivity.navController
        binding.lbProceed.setOnClickListener {
            NavigationManager.navigateToFragment(
                navController, R.id.CheckoutFragment
            )

        }


        binding.mcvGotoPackages.setOnClickListener {
            NavigationManager.navigateToFragment(
                navController, R.id.PurchasePackage
            )

        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = RetrofitClient.client // Initialize API service
        setupRecyclerView()


        fetchCartData()
    }

    private fun setupRecyclerView() {
        val cartAdapter = CartAdapter(cartItems,true) { position ->

                  handleDelete(position)
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun fetchCartData() {
        showLoading()

        val authToken = "Bearer $token"

        apiService.getCartData(authToken).enqueue(object : Callback<AllCartResponse> {
            override fun onResponse(call: Call<AllCartResponse>, response: Response<AllCartResponse>) {
                hideLoading() // Hide ProgressBar and show views when the API call completes
                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            val allCartResponse = response.body()
                            allCartResponse?.let {
                                if (it.cartCount < 1) {
                                    isCartEmpty(true)
                                    updateCartCount(it.cartCount)
                                } else {
                                    isCartEmpty(false)
                                    updateCartCount(it.cartCount)
                                    updateCartList(it.cartList ?: emptyList())
                                    it.summary?.let { summary -> updateSummary(summary) }
                                }
                            }
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            // Handle unauthorized response
                            Utils.UnAuthorizationToken(requireContext())
                        }
                        else -> {
                            // Log any other response codes
                            E("fetchCartData: Error code ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    E("fetchCartData: Exception ${e.message}")
                }
            }

            override fun onFailure(call: Call<AllCartResponse>, t: Throwable) {
                hideLoading()
                t.printStackTrace()
                E("fetchCartData: Failure ${t.message}")
            }
        })
    }


    private fun updateCartList(cartList: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(cartList)
        binding.recyclerViewCart.adapter?.notifyDataSetChanged()
    }

    private fun updateSummary(summary: CartSummary) {

        binding.tvSubtotal.text = "₹${summary.subTotal ?: "0.0"}"
        binding.tvDiscount.text = "- ₹${summary.discountAmt ?: "0.0"}"
        binding.tvTotal.text = "₹${summary.grandTotal ?: "0.0"}"
    }

    private fun handleDelete(cartID: String) {
        showLoading()

        val authToken = "Bearer $token"  // Assuming your token is stored in the token variable

        apiService.deleteCartItem(authToken, cartID).enqueue(object : Callback<DeleteCartResponse> {
            override fun onResponse(call: Call<DeleteCartResponse>, response: Response<DeleteCartResponse>) {
                hideLoading() // Ensure loading is hidden

                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            fetchCartData() // Refresh cart after successful deletion
                        }

                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.UnAuthorizationToken(requireContext()) // Handle unauthorized case
                        }

                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                E("handleDelete BAD_REQUEST: $errorMessage")
                            }
                        }

                        else -> {
                            E("handleDelete Error: ${response.code()} - ${response.errorBody()?.string()}")
                        }
                    }
                } catch (e: Exception) {
                    E("handleDelete Exception: ${e.message}")
                }
            }

            override fun onFailure(call: Call<DeleteCartResponse>, t: Throwable) {
                hideLoading()
                E("handleDelete Failure: ${t.message}")
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showLoading() {
        // Hide all views and show the ProgressBar
        binding.llCartContent.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        // Show all views and hide the ProgressBar
        binding.llCartContent.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }


    private fun updateCartCount(cartCount:Int) {
        (context as? DashboardActivity)?.updateCartCount(cartCount)
    }

    private fun isCartEmpty(isCartEmpty: Boolean) {
        if (isCartEmpty) {
            // If the cart is empty, show the empty cart layout and hide the cart content
            binding.llCartContent.visibility = View.GONE
            binding.llEmptyCartLayout.visibility = View.VISIBLE
        } else {
            // If the cart has items, show the cart content and hide the empty cart layout
            binding.llCartContent.visibility = View.VISIBLE
            binding.llEmptyCartLayout.visibility = View.GONE
        }
    }


}
