package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
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
import com.anurupjaiswal.learnandachieve.basic.validation.Validation
import com.anurupjaiswal.learnandachieve.basic.validation.ValidationModel
import com.anurupjaiswal.learnandachieve.databinding.FragmentCartBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.main_package.ui.activity.DashboardActivity
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.CartItem
import com.anurupjaiswal.learnandachieve.model.CartSummary
import com.anurupjaiswal.learnandachieve.model.CheckReferralResponse
import com.anurupjaiswal.learnandachieve.model.DeleteCartResponse
import com.google.gson.Gson

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment(), View.OnClickListener  {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val errorValidationModels = mutableListOf<ValidationModel>()
 private  var  isReferralApplied : Boolean = false
    private val cartItems = mutableListOf<CartItem>()
    private var currentSummary: CartSummary? = null

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
            val bundle = Bundle().apply {
                putBoolean("isReferralApplied", isReferralApplied)
            }
            NavigationManager.navigateToFragment(navController, R.id.CheckoutFragment,bundle)

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

         binding.mcvApply.setOnClickListener(this)
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
                                    currentSummary = it.summary
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
        binding.tvTotal.text = "₹${if (isReferralApplied) summary.grandTotalCoordinator else summary.grandTotal ?: "0.0"}"
        binding.tvSubtotal.text = "₹${summary.subTotal ?: "0.0"}"
        binding.llDiscount.apply {
            visibility = if (isReferralApplied) View.VISIBLE else View.GONE
        }
        binding.tvDiscount.text = "- ₹${summary.discountAmt ?: "0.0"}"
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


    private fun checkReferralCode(referralCode: String) {
        Utils.toggleProgressBarAndText(true, binding.referralloading, binding.tvApply, binding.root)


        val referralData = hashMapOf<String, String>().apply {
            put("referralCode", referralCode)
        }


        val authToken = "Bearer ${Utils.GetSession().token}"


        apiService.checkReferralCode(authToken, referralData).enqueue(object : Callback<CheckReferralResponse> {
            override fun onResponse(
                call: Call<CheckReferralResponse>,
                response: Response<CheckReferralResponse>
            ) {
                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            // Successful response
                            response.body()?.let { checkReferralResponse ->
                                E("Response: ${checkReferralResponse.message}")
                                Utils.T(requireContext(),checkReferralResponse.message)
                                Utils.toggleProgressBarAndText(false, binding.referralloading, binding.tvApply, binding.root)
                                isReferralApplied = true
                                currentSummary?.let { updateSummary(it) }

                            }
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            isReferralApplied = false
                            currentSummary?.let { updateSummary(it) }

                            Utils.toggleProgressBarAndText(false, binding.referralloading, binding.tvApply, binding.root)
                            E("Unauthorized: ${response.message()}")
                            Utils.UnAuthorizationToken(requireContext())
                        }
                        StatusCodeConstant.BAD_REQUEST -> {
                            Utils.toggleProgressBarAndText(false, binding.referralloading, binding.tvApply, binding.root)
                            isReferralApplied = false
                            currentSummary?.let { updateSummary(it) }
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val displayMessage = apiError.error ?: "Unknown error"
                                Utils.T(requireContext(), displayMessage)
                                E(displayMessage)
                            }
                        }
                        else -> {
                            isReferralApplied = false
                            Utils.toggleProgressBarAndText(false, binding.referralloading, binding.tvApply, binding.root)
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(errorBody.charStream(), APIError::class.java)
                                val errorMessage = apiError.error ?: "Something went wrong"
                               E( "Response: $errorMessage")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Utils.toggleProgressBarAndText(false, binding.referralloading, binding.tvApply, binding.root)
                    e.printStackTrace()
                   E( "An error occurred: ${e.message}")
                }
            }
            override fun onFailure(call: Call<CheckReferralResponse>, t: Throwable) {
                Utils.toggleProgressBarAndText(false, binding.referralloading, binding.tvApply, binding.root)

                E("Error: ${t.message}")
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



    override fun onClick(view: View) {
        if (view == binding.mcvApply) {

            errorValidationModels.clear()
            errorValidationModels.add(
                ValidationModel(
                    Validation.Type.Empty, binding.etReferral, binding.tvRefferralEror
                )
            )



            val validation = Validation.instance
            val validationResult = validation?.CheckValidation(requireContext(), errorValidationModels)

            if (validationResult?.aBoolean == true) {
                checkReferralCode(binding.etReferral.text.toString().trim())
            } else {
                binding.tvRefferralEror.visibility = View.VISIBLE
                binding.tvRefferralEror.text =
                    validationResult?.errorMessage ?: validation?.errorMessage
                binding.tvRefferralEror.startAnimation(
                    AnimationUtils.loadAnimation(requireContext(), R.anim.top_to_bottom)
                )

                validation?.EditTextPointer?.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(validation?.EditTextPointer, InputMethodManager.SHOW_IMPLICIT)

            }
        }


    }



}
