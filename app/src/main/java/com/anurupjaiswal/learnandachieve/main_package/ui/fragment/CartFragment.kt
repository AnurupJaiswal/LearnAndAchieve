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
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.databinding.FragmentCartBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.CartItem
import com.anurupjaiswal.learnandachieve.model.CartSummary
import com.anurupjaiswal.learnandachieve.model.DeleteCartResponse

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartItems = mutableListOf<CartItem>()

    private lateinit var navController: NavController

    private var Token: String? = null

    private lateinit var apiService: ApiService

    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        Token = Utils.GetSession().token
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
        val cartAdapter = CartAdapter(cartItems) { position ->

                  handleDelete(position)
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun fetchCartData() {
        showLoading()

        val authToken = "Bearer $Token"

        apiService.getCartData(authToken).enqueue(object : Callback<AllCartResponse> {
            override fun onResponse(call: Call<AllCartResponse>, response: Response<AllCartResponse>) {
                hideLoading() // Hide ProgressBar and show views when the API fails
                try {
                    if (response.code() == StatusCodeConstant.OK) {
                        val allCartResponse = response.body()

                        allCartResponse?.let {
                            // Check if cartCount is less than 1 and show empty cart view
                            if (it.cartCount < 1) {
                                isCartEmpty(true)
                                updateCartCount(allCartResponse.cartCount)

                            } else {
                                isCartEmpty(false)

                                updateCartCount(allCartResponse.cartCount)
                                updateCartList(it.cartList ?: emptyList())
                                allCartResponse.summary?.let { summary -> updateSummary(summary) }

                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<AllCartResponse>, t: Throwable) {
                hideLoading() // Hide ProgressBar and show views when the API fails
                t.printStackTrace()
                Toast.makeText(requireContext(), "Failed to fetch cart data", Toast.LENGTH_SHORT).show()
            }
        })    }

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

    private fun handleDelete(cart_id :String) {
        showLoading()

        val authToken = "Bearer $Token"  // Assuming your token is stored in the Token variable
        val packageId = cart_id // The ID of the item to delete

        // Make the DELETE API call
        apiService.deleteCartItem(authToken, packageId).enqueue(object : Callback<DeleteCartResponse> {
            override fun onResponse(call: Call<DeleteCartResponse>, response: Response<DeleteCartResponse>) {
                if (response.isSuccessful) {
                    hideLoading()
                    fetchCartData()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DeleteCartResponse>, t: Throwable) {

                hideLoading()

                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun navigateBack() {
        findNavController().popBackStack()
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
