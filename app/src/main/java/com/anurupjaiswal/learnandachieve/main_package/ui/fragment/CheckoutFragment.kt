package com.anurupjaiswal.learnandachieve.main_package.ui.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurupjaiswal.learnandachieve.R
import com.anurupjaiswal.learnandachieve.basic.retrofit.ApiService
import com.anurupjaiswal.learnandachieve.basic.retrofit.Const
import com.anurupjaiswal.learnandachieve.basic.retrofit.RetrofitClient
import com.anurupjaiswal.learnandachieve.basic.utilitytools.NavigationManager
import com.anurupjaiswal.learnandachieve.basic.utilitytools.StatusCodeConstant
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils
import com.anurupjaiswal.learnandachieve.basic.utilitytools.Utils.toggleProgressBarAndText
import com.anurupjaiswal.learnandachieve.databinding.FragmentCheckoutBinding
import com.anurupjaiswal.learnandachieve.main_package.adapter.CartAdapter
import com.anurupjaiswal.learnandachieve.model.AllCartResponse
import com.anurupjaiswal.learnandachieve.model.CartItem
import com.anurupjaiswal.learnandachieve.model.CartSummary
import com.anurupjaiswal.learnandachieve.model.CreateOrderRequest
import com.anurupjaiswal.learnandachieve.model.CreateOrderResponse
import com.anurupjaiswal.learnandachieve.model.Notes
import com.anurupjaiswal.learnandachieve.model.PackageModel
import com.anurupjaiswal.learnandachieve.model.VerifyPaymentResponse
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response








class CheckoutFragment : Fragment(), PaymentResultListener {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private var token: String? = null
    private val cartItems = mutableListOf<CartItem>()

    private lateinit var apiService: ApiService
    private var grandTotal: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiService = RetrofitClient.client // Initialize API service
        cartItems.clear()
        token = Utils.GetSession().token
        setupRecyclerView()
        fetchCartData()
        setupTermsText()

        // Razorpay Checkout initialization
        Checkout.preload(requireContext())

        // Handle the checkout button click
        binding.proceedButton.setOnClickListener {
            if (grandTotal.toDoubleOrNull()?.times(100)?.toInt() ?: 0 > 0) {
                //startPayment()
                createOrder(grandTotal)
            } else {
                Toast.makeText(requireContext(), "Cart is empty or invalid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupTermsText() {
        val text = "By completing your purchase you agree to these Terms of Service."
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                NavigationManager.navigateToFragment(findNavController(), R.id.termsAndConditionsFragment)
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
            override fun onResponse(call: Call<AllCartResponse>, response: Response<AllCartResponse>) {
                if (response.code() == StatusCodeConstant.OK) {
                    val allCartResponse = response.body()
                    allCartResponse?.let {
                        allCartResponse.cartList?.let { cartItems.addAll(it) }
                        allCartResponse.summary?.let { summary -> updateSummary(summary) }
                        binding.recyclerViewCart.adapter?.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AllCartResponse>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(requireContext(), "Failed to fetch cart data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSummary(summary: CartSummary) {
        grandTotal = (summary.grandTotal ?: 0.0).toString()
        binding.tvSubtotal.text = "₹${summary.subTotal ?: "0.0"}"
        binding.tvDiscount.text = "- ₹${summary.discountAmt ?: "0.0"}"
        binding.tvTotal.text = "₹${summary.grandTotal ?: "0.0"}"
    }

    private fun startPayment(
        orderId: String,
        purchaseAmount: Int,
        currency: String,
        receipt: String,
        entity: String
    ) {
        val checkout = Checkout()
        checkout.setKeyID(Const.razorpayKeyName)  // Use Razorpay key

        try {
            val amountInPaise = purchaseAmount * 100  // Convert to paise

            val options = JSONObject().apply {
                put("key", Const.razorpayKeyName)  // Razorpay Key
                put("amount", purchaseAmount)  // Purchase amount in paise
                put("description", "Testing")
                put("name", "${Utils.GetSession().firstName} ${Utils.GetSession().lastName}")  // User's full name
                put("order_id", orderId)  // Order ID
                put("timeout", 60 * 2)  // Timeout of 2 minutes (in seconds)

                // Prefill user details
                val prefill = JSONObject().apply {
                    put("contact", Utils.GetSession().mobile)  // User's mobile number
                    put("email",  Utils.GetSession().email)    // User's email address
                }
                put("prefill", prefill)

                put("receipt", receipt)  // Pass receipt
                put("entity", entity)    // Pass entity
            }

            checkout.open(requireActivity(), options)  // Open Razorpay checkout

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error in payment: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try {
            // The razorpayPaymentId is passed as an argument to this method
            val paymentId = razorpayPaymentId ?: ""

            val data = requireActivity().intent.extras // Use requireActivity() for Fragment
            val razorpayOrderId = data?.getString("razorpay_order_id") ?: ""  // Order ID from extras
            val razorpaySignature = data?.getString("razorpay_signature") ?: ""  // Signature from extras

            // Display success message
            Utils.T(requireContext(), "Payment Successful")

            // Log the payment details for debugging
            Log.d("PaymentSuccess", "Payment ID: $paymentId")
            Log.d("PaymentSuccess", "Order ID: $razorpayOrderId")
            Log.d("PaymentSuccess", "Signature: $razorpaySignature")

            // Send this data to your backend for verification
            verifyPayment(razorpayOrderId, paymentId, razorpaySignature)

        } catch (e: Exception) {
            e.printStackTrace()
            Utils.T(requireContext(), "Error retrieving payment details: ${e.message}")
        }
    }

    override fun onPaymentError(code: Int, description: String?) {

        Utils.T(requireContext(),"Payment Failed: $description")
        // Handle payment failure
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createOrder(grandTotal:String) {
        toggleProgressBarAndText(true, binding.loading, binding.tvPay,binding.root)


        val oderData = HashMap<String, String>().apply {
            put("amount", grandTotal)

            put("currency", "INR")  // Add any other necessary fields
        }

        val authToken = "Bearer ${Utils.GetSession().token}"  // Assuming the token is stored in session
        if (token != null) {
            apiService.createOrder(authToken,oderData).enqueue(object : Callback<CreateOrderResponse> {
                override fun onResponse(
                    call: Call<CreateOrderResponse>,
                    response: Response<CreateOrderResponse>
                ) {
                    if (response.isSuccessful) {
                        val orderResponse = response.body()
                        Log.d("API_SUCCESS", "Order ID: ${orderResponse?.id}")
                        Log.d("API_SUCCESS", "Amount Due: ${orderResponse?.amount_due}")
                        Log.d("API_SUCCESS", "Currency: ${orderResponse?.currency}")
                        Log.d("API_SUCCESS", "User ID: ${orderResponse?.notes?.userId}")
                        val orderId = orderResponse?.id ?: return
                        val amountDue = orderResponse?.amount_due ?: return
                        val currency = orderResponse?.currency ?: "INR"
                        val userId = orderResponse?.notes?.userId ?: ""
                        val receipt = orderResponse?.receipt ?: ""
                        val entity = orderResponse?.entity ?: ""

                        startPayment(orderId, amountDue, currency, receipt, entity)


                        toggleProgressBarAndText(false, binding.loading, binding.tvPay,binding.root)

                    } else {
                        toggleProgressBarAndText(false, binding.loading, binding.tvPay,binding.root)

                        Log.e("API_ERROR", "Response Code: ${response.code()}, Message: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {


                    Log.e("API_FAILURE", "Error: ${t.message}")
                }
            })
        }
    }



    private fun verifyPayment(orderId: String, razorpayPaymentId: String, razorpaySignature: String) {
        val paymentData = HashMap<String, String>().apply {
            put("razorpay_order_id", orderId)
            put("razorpay_payment_id", razorpayPaymentId)
            put("razorpay_signature", razorpaySignature)
            put("referralCode", "")  // Add any other necessary fields
        }

        val authToken = "Bearer ${Utils.GetSession().token}"  // Assuming the token is stored in session

        apiService.verifyPayment(authToken, paymentData).enqueue(object : Callback<VerifyPaymentResponse> {
            override fun onResponse(call: Call<VerifyPaymentResponse>, response: Response<VerifyPaymentResponse>) {
                if (response.isSuccessful) {
                    // Handle success
                    val verifyPaymentResponse = response.body()
                    Log.d("PaymentVerification", "Response: ${verifyPaymentResponse?.message}")
                    // Additional logic after payment verification
                } else {
                    // Handle failure
                    Log.e("PaymentVerification", "Error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<VerifyPaymentResponse>, t: Throwable) {
                // Handle failure
                Log.e("PaymentVerification", "Error: ${t.message}")
            }
        })
    }
}


