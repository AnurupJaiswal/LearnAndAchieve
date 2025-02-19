package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.APIError
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.E
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.toggleProgressBarAndText
import com.anurupjaiswal.learnandachieve.databinding.FragmentCheckoutBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.CartItem
import com.anurupjaiswal.learnandachieve.model.CartSummary
import com.anurupjaiswal.learnandachieve.model.CreateOrderResponse
import com.anurupjaiswal.learnandachieve.model.VerifyPaymentResponse
import com.google.gson.Gson
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutFragment : androidx.fragment.app.Fragment(), PaymentResultListener {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private var token: String? = null
    private val cartItems = mutableListOf<CartItem>()
    private lateinit var apiService: ApiService
    private var grandTotal: String = ""
    // Store current order ID for later verification.
    private var currentOrderId: String = ""
    private var isReferralApplied: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isReferralApplied = arguments?.getBoolean("isReferralApplied", false) ?: false

        apiService = RetrofitClient.client
        cartItems.clear()
        token = Utils.GetSession().token
        setupRecyclerView()
        fetchCartData()
        setupTermsText()

        // Preload Razorpay Checkout.
        Checkout.preload(requireContext())

        binding.proceedButton.setOnClickListener {
            if (!binding.checkbox.isChecked) {

                Utils.T(requireContext(), "Please agree to the terms before proceeding")
            } else {
                // If checkbox is checked, then validate grandTotal
                if (grandTotal.toDoubleOrNull()?.times(100)?.toInt() ?: 0 > 0) {
                    createOrder(grandTotal)
                } else {
                    Utils.T(requireContext(), "Cart is empty or invalid amount")
                }
            }
        }

    }

    private fun setupTermsText() {
        val text = "By completing your purchase you agree to these Terms of Service."
        val spannableString = SpannableString(text)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                NavigationManager.navigateToFragment(
                    findNavController(),
                    R.id.termsAndConditionsFragment
                )
            }
            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.blue)
            }
        }
        val startIndex = text.indexOf("Terms of Service")
        val endIndex = startIndex + "Terms of Service".length
        val boldSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(boldSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textTerms.text = spannableString
        binding.textTerms.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupRecyclerView() {
        val cartAdapter = CartAdapter(cartItems, false) { position -> }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun fetchCartData() {
        val authToken = "Bearer $token"
        apiService.getCartData(authToken).enqueue(object : Callback<AllCartResponse> {
            override fun onResponse(
                call: Call<AllCartResponse>,
                response: Response<AllCartResponse>
            ) {
                try {
                    when (response.code()) {
                        StatusCodeConstant.OK -> {
                            val allCartResponse = response.body()
                            allCartResponse?.let {
                                it.cartList?.let { cartItems.addAll(it) }
                                it.summary?.let { summary -> updateSummary(summary) }
                                binding.recyclerViewCart.adapter?.notifyDataSetChanged()
                            }
                        }
                        StatusCodeConstant.UNAUTHORIZED -> {
                            Utils.UnAuthorizationToken(requireContext())
                        }
                        StatusCodeConstant.BAD_REQUEST -> {
                            response.errorBody()?.let { errorBody ->
                                val apiError = Gson().fromJson(
                                    errorBody.charStream(),
                                    APIError::class.java
                                )
                                val errorMessage = apiError.error ?: "Bad Request Error"
                                E("fetchCartData BAD_REQUEST: $errorMessage")
                            }
                        }
                        else -> {
                            E("fetchCartData Error: ${response.code()} - ${response.errorBody()?.string()}")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    E("fetchCartData Exception: ${e.message}")
                }
            }
            override fun onFailure(call: Call<AllCartResponse>, t: Throwable) {
                t.printStackTrace()
                E("fetchCartData Failure: ${t.message}")
            }
        })
    }
    private fun updateSummary(summary: CartSummary) {
        grandTotal = (summary.grandTotal ?: 0.0).toString()
        binding.tvTotal.text = "₹${if (isReferralApplied) summary.grandTotalCoordinator else summary.grandTotal ?: "0.0"}"
        binding.tvSubtotal.text = "₹${summary.subTotal ?: "0.0"}"
        binding.llDiscount.apply {
            visibility = if (isReferralApplied) View.VISIBLE else View.GONE
        }
        binding.tvDiscount.text = "- ₹${summary.discountAmt ?: "0.0"}"
    }
    private fun startPayment(
        orderId: String,
        purchaseAmount: Int,
        currency: String,
        receipt: String,
        entity: String
    ) {
        val checkout = Checkout()
        checkout.setKeyID(Const.razorpayKeyName)
        try {
            val options = JSONObject().apply {
                put("key", Const.razorpayKeyName)
                put("amount", purchaseAmount) // Amount in paise.
                put("description", "Purchase")
                put("name", "${Utils.GetSession().firstName} ${Utils.GetSession().lastName}")
                put("order_id", orderId)
                put("timeout", 60 * 2)
                val prefill = JSONObject().apply {
                    put("contact", Utils.GetSession().mobile)
                    put("email", Utils.GetSession().email)
                }
                put("prefill", prefill)
                put("receipt", receipt)
                put("entity", entity)
            }
            checkout.open(requireActivity(), options)
        } catch (e: Exception) {
            e.printStackTrace()
            E("Error in payment: ${e.message}")
        }
    }

    // Called when payment succeeds (forwarded from DashboardActivity).
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        val paymentId = razorpayPaymentId ?: ""
        Utils.T(requireContext(), "Payment Successful")
        E("Payment ID: $paymentId")
        val navOptions = NavOptions.Builder()
            .setPopUpTo(findNavController().graph.startDestinationId, inclusive = true)
            .build()
        findNavController().navigate(R.id.home, null, navOptions)
        verifyPayment(currentOrderId, paymentId, "")
    }

    override fun onPaymentError(code: Int, description: String?) {
        Utils.T(requireContext(), "Payment Failed: $description")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createOrder(grandTotal: String) {
        toggleProgressBarAndText(true, binding.loading, binding.tvPay, binding.root)
        val orderData = hashMapOf<String, String>().apply {
            put("amount", grandTotal)
            put("currency", "INR")
        }
        val authToken = "Bearer ${Utils.GetSession().token}"
        if (token != null) {
            apiService.createOrder(authToken, orderData).enqueue(object : Callback<CreateOrderResponse> {
                override fun onResponse(
                    call: Call<CreateOrderResponse>,
                    response: Response<CreateOrderResponse>
                ) {
                    toggleProgressBarAndText(false, binding.loading, binding.tvPay, binding.root)
                    if (response.isSuccessful) {
                        val orderResponse = response.body()
                        E("Order ID: ${orderResponse?.id}")
                        // Save the order ID for later verification.
                        currentOrderId = orderResponse?.id ?: ""
                        val amountDue = orderResponse?.amount_due ?: return
                        val currency = orderResponse?.currency ?: "INR"
                        val receipt = orderResponse?.receipt ?: ""
                        val entity = orderResponse?.entity ?: ""
                        startPayment(currentOrderId, amountDue, currency, receipt, entity)
                    } else {
                        E("Response Code: ${response.code()}, Message: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                    toggleProgressBarAndText(false, binding.loading, binding.tvPay, binding.root)
                    E("Error: ${t.message}")
                }
            })
        }
    }

    private fun verifyPayment(orderId: String, razorpayPaymentId: String, razorpaySignature: String) {
        val paymentData = hashMapOf<String, String>().apply {
            put("razorpay_order_id", orderId)
            put("razorpay_payment_id", razorpayPaymentId)
            put("razorpay_signature", razorpaySignature)
            put("referralCode", "")
        }
        val authToken = "Bearer ${Utils.GetSession().token}"
        apiService.verifyPayment(authToken, paymentData).enqueue(object : Callback<VerifyPaymentResponse> {
            override fun onResponse(
                call: Call<VerifyPaymentResponse>,
                response: Response<VerifyPaymentResponse>
            ) {
                if (response.isSuccessful) {
                    val verifyPaymentResponse = response.body()
                    E("Response: ${verifyPaymentResponse?.message}")
                    // Clear the entire backstack and navigate to Home.
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(findNavController().graph.startDestinationId, inclusive = true)
                        .build()
                    findNavController().navigate(R.id.home, null, navOptions)
                } else {
                    E("Error: ${response.code()} ${response.message()}")
                    Utils.T(requireContext(), "Payment verification failed.")
                }
            }
            override fun onFailure(call: Call<VerifyPaymentResponse>, t: Throwable) {
                E("Error: ${t.message}")
                Utils.T(requireContext(), "Payment verification failed: ${t.message}")
            }
        })
    }
}
